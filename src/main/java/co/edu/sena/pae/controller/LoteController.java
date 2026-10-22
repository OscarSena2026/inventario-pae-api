package co.edu.sena.pae.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import co.edu.sena.pae.dto.LoteDTO;
import co.edu.sena.pae.model.Lote;
import co.edu.sena.pae.service.LoteService;

/**
 * Endpoints REST del recurso lote (/api/lotes).
 * El controlador recibe la peticion, llama al servicio y responde con el codigo HTTP.
 */
@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteService service;

    public LoteController(LoteService service) {
        this.service = service;
    }

    // GET /api/lotes -> 200 + lista
    @GetMapping
    public List<Lote> listar() {
        return service.listar();
    }

    // GET /api/lotes/{id} -> 200 + registro, 404 si no existe
    @GetMapping("/{id}")
    public Lote obtener(@PathVariable("id") Integer id) {
        return service.obtener(id);
    }

    // POST /api/lotes -> 201 + registro creado, 400 datos invalidos, 404 referencia inexistente
    @PostMapping
    public ResponseEntity<Lote> crear(@Valid @RequestBody LoteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    // PUT /api/lotes/{id} -> 200 + registro actualizado, 400 o 404
    @PutMapping("/{id}")
    public Lote actualizar(@PathVariable("id") Integer id, @Valid @RequestBody LoteDTO dto) {
        return service.actualizar(id, dto);
    }

    // DELETE /api/lotes/{id} -> 204 sin contenido, 404 si no existe, 409 si tiene registros relacionados
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
