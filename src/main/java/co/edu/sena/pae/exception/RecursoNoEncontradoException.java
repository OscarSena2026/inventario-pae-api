package co.edu.sena.pae.exception;

/**
 * Se lanza cuando un registro no existe (responde 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
