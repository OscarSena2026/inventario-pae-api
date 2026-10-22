package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.UsuarioRespuestaDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Usuario;
import co.edu.sena.pae.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestion de usuarios por el administrador (HU-07): listar, activar y desactivar.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioRespuestaDTO> listar() {
        return usuarioRepository.findAll().stream().map(this::convertir).toList();
    }

    // Activa o desactiva un usuario; un usuario desactivado no puede iniciar sesion
    @Transactional
    public UsuarioRespuestaDTO cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Usuario con id " + id + " no existe"));
        usuario.setActivo(activo);
        return convertir(usuarioRepository.save(usuario));
    }

    // Convierte la entidad en datos publicos (sin contrasena)
    private UsuarioRespuestaDTO convertir(Usuario u) {
        UsuarioRespuestaDTO dto = new UsuarioRespuestaDTO();
        dto.setId(u.getId());
        dto.setNombreUsuario(u.getNombreUsuario());
        dto.setRol(u.getRol());
        dto.setActivo(u.getActivo());
        return dto;
    }
}
