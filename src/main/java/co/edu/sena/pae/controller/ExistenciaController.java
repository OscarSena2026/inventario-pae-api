package co.edu.sena.pae.controller;

import co.edu.sena.pae.dto.ExistenciaRespuestaDTO;
import co.edu.sena.pae.service.ExistenciaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de consulta del stock disponible (/api/existencias).
 */
@RestController
@RequestMapping("/api/existencias")
public class ExistenciaController {

    private final ExistenciaService service;

    public ExistenciaController(ExistenciaService service) {
        this.service = service;
    }

    // GET /api/existencias -> 200 + lista
    @GetMapping
    public List<ExistenciaRespuestaDTO> listar() {
        return service.listar();
    }

    // GET /api/existencias/bodega/{idBodega} -> 200 + lista, 404 si la bodega no existe
    @GetMapping("/bodega/{idBodega}")
    public List<ExistenciaRespuestaDTO> listarPorBodega(@PathVariable("idBodega") Integer idBodega) {
        return service.listarPorBodega(idBodega);
    }
}
