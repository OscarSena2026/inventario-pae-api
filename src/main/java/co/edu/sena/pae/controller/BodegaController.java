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
import co.edu.sena.pae.dto.BodegaDTO;
import co.edu.sena.pae.model.Bodega;
import co.edu.sena.pae.service.BodegaService;

/**
 * Endpoints REST del recurso bodega (/api/bodegas).
 * El controlador recibe la peticion, llama al servicio y responde con el codigo HTTP.
 */
@RestController
@RequestMapping("/api/bodegas")
public class BodegaController {

    private final BodegaService service;

    public BodegaController(BodegaService service) {
        this.service = service;
    }

    // GET /api/bodegas -> 200 + lista
    @GetMapping
    public List<Bodega> listar() {
        return service.listar();
    }

    // GET /api/bodegas/{id} -> 200 + registro, 404 si no existe
    @GetMapping("/{id}")
    public Bodega obtener(@PathVariable("id") Integer id) {
        return service.obtener(id);
    }

    // POST /api/bodegas -> 201 + registro creado, 400 datos invalidos, 404 referencia inexistente
    @PostMapping
    public ResponseEntity<Bodega> crear(@Valid @RequestBody BodegaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    // PUT /api/bodegas/{id} -> 200 + registro actualizado, 400 o 404
    @PutMapping("/{id}")
    public Bodega actualizar(@PathVariable("id") Integer id, @Valid @RequestBody BodegaDTO dto) {
        return service.actualizar(id, dto);
    }

    // DELETE /api/bodegas/{id} -> 204 sin contenido, 404 si no existe, 409 si tiene registros relacionados
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
