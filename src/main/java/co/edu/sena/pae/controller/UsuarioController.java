package co.edu.sena.pae.controller;

import co.edu.sena.pae.dto.UsuarioRespuestaDTO;
import co.edu.sena.pae.service.UsuarioService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestion de usuarios (/api/usuarios) - HU-07.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // GET /api/usuarios -> 200 + lista (sin contrasenas)
    @GetMapping
    public List<UsuarioRespuestaDTO> listar() {
        return service.listar();
    }

    // PUT /api/usuarios/{id}/desactivar -> 200 + usuario, 404 si no existe
    @PutMapping("/{id}/desactivar")
    public UsuarioRespuestaDTO desactivar(@PathVariable("id") Long id) {
        return service.cambiarEstado(id, false);
    }

    // PUT /api/usuarios/{id}/activar -> 200 + usuario, 404 si no existe
    @PutMapping("/{id}/activar")
    public UsuarioRespuestaDTO activar(@PathVariable("id") Long id) {
        return service.cambiarEstado(id, true);
    }
}
