package co.edu.sena.pae.controller;

import co.edu.sena.pae.dto.EntradaDTO;
import co.edu.sena.pae.dto.EntradaRespuestaDTO;
import co.edu.sena.pae.service.EntradaService;
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
 * Endpoints de entradas de stock (/api/entradas) - HU-03.
 * Los movimientos no se modifican ni se eliminan: garantizan la trazabilidad del inventario.
 */
@RestController
@RequestMapping("/api/entradas")
public class EntradaController {

    private final EntradaService service;

    public EntradaController(EntradaService service) {
        this.service = service;
    }

    // GET /api/entradas -> 200 + lista
    @GetMapping
    public List<EntradaRespuestaDTO> listar() {
        return service.listar();
    }

    // GET /api/entradas/{id} -> 200 + registro, 404 si no existe
    @GetMapping("/{id}")
    public EntradaRespuestaDTO obtener(@PathVariable("id") Integer id) {
        return service.obtener(id);
    }

    // POST /api/entradas -> 201 creada, 400 datos invalidos o regla de negocio, 404 referencia inexistente
    @PostMapping
    public ResponseEntity<EntradaRespuestaDTO> registrar(@Valid @RequestBody EntradaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }
}
