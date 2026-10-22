package co.edu.sena.pae.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Convierte las excepciones en respuestas JSON claras con su codigo HTTP:
 * 400 datos invalidos o regla de negocio, 404 no existe, 409 duplicado o con registros relacionados.
 */
@RestControllerAdvice
public class ManejadorErrores {

    // Arma el cuerpo JSON de la respuesta de error
    private ResponseEntity<Map<String, Object>> responder(HttpStatus estado, String mensaje, Map<String, String> errores) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estado", estado.value());
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        if (errores != null && !errores.isEmpty()) {
            cuerpo.put("errores", errores);
        }
        return ResponseEntity.status(estado).body(cuerpo);
    }

    // Falla alguna validacion del DTO (@NotBlank, @Positive, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos", errores);
    }

    // El JSON esta mal escrito, falta el body o un dato tiene formato incorrecto (ej: fecha)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> cuerpoInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST,
                "Cuerpo de la petición inválido: revise el JSON, las fechas (AAAA-MM-DD) y los números", null);
    }

    // El id de la ruta no es un numero (ej: /api/productos/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El valor del parámetro '" + ex.getName() + "' no es válido", null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<Map<String, Object>> conflicto(ConflictoException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(ReglaNegocioException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // Restriccion de la base de datos: dato duplicado o registro con dependencias
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException ex) {
        return responder(HttpStatus.CONFLICT,
                "La operación viola una restricción de datos: el registro está duplicado o tiene registros relacionados", null);
    }
}
