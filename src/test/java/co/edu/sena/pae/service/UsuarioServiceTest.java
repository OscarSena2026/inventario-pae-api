package co.edu.sena.pae.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.sena.pae.dto.UsuarioRespuestaDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Usuario;
import co.edu.sena.pae.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de UsuarioService (activar y desactivar usuarios).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private UsuarioService servicio;

    private Usuario usuario(boolean activo) {
        Usuario u = new Usuario();
        u.setId(7L);
        u.setNombreUsuario("oscar");
        u.setContrasena("$2a$10$hashDePrueba");
        u.setRol("USUARIO");
        u.setActivo(activo);
        return u;
    }

    @Test
    void cambiarEstado_desactivaAlUsuario() {
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario(true)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioRespuestaDTO respuesta = servicio.cambiarEstado(7L, false);

        assertEquals(Boolean.FALSE, respuesta.getActivo());
        assertEquals("oscar", respuesta.getNombreUsuario());
    }

    @Test
    void cambiarEstado_activaAlUsuario() {
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario(false)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals(Boolean.TRUE, servicio.cambiarEstado(7L, true).getActivo());
    }

    @Test
    void cambiarEstado_fallaSiElUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.cambiarEstado(99L, false));

        assertEquals("Usuario con id 99 no existe", error.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
