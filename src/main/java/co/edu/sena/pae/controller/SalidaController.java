package co.edu.sena.pae.controller;

import co.edu.sena.pae.dto.SalidaDTO;
import co.edu.sena.pae.dto.SalidaRespuestaDTO;
import co.edu.sena.pae.service.SalidaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de salidas de stock (/api/salidas) - HU-04.
 * Los movimientos no se modifican ni se eliminan: garantizan la trazabilidad del inventario.
 */
@RestController
@RequestMapping("/api/salidas")
public class SalidaController {

    private final SalidaService service;

    public SalidaController(SalidaService service) {
        this.service = service;
    }

    // GET /api/salidas -> 200 + lista
    @GetMapping
    public List<SalidaRespuestaDTO> listar() {
        return service.listar();
    }

    // GET /api/salidas/{id} -> 200 + registro, 404 si no existe
    @GetMapping("/{id}")
    public SalidaRespuestaDTO obtener(@PathVariable("id") Integer id) {
        return service.obtener(id);
    }

    // POST /api/salidas -> 201 creada, 400 datos invalidos o regla de negocio, 404 referencia inexistente, 409 stock insuficiente
    @PostMapping
    public ResponseEntity<SalidaRespuestaDTO> registrar(@Valid @RequestBody SalidaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }
}
