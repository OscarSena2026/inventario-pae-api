package co.edu.sena.pae.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.sena.pae.dto.CredencialesDTO;
import co.edu.sena.pae.dto.RegistroUsuarioDTO;
import co.edu.sena.pae.exception.ConflictoException;
import co.edu.sena.pae.model.Usuario;
import co.edu.sena.pae.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Pruebas unitarias de AuthService (HU-01: registro e inicio de sesión con contraseña cifrada con BCrypt).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private AuthService servicio;

    private final BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    private RegistroUsuarioDTO registro(String usuario, String contrasena, String rol) {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setUsuario(usuario);
        dto.setContrasena(contrasena);
        dto.setRol(rol);
        return dto;
    }

    private CredencialesDTO credenciales(String usuario, String contrasena) {
        CredencialesDTO dto = new CredencialesDTO();
        dto.setUsuario(usuario);
        dto.setContrasena(contrasena);
        return dto;
    }

    // Usuario guardado con la contraseña ya cifrada, como queda en la base de datos.
    private Usuario usuarioGuardado(String contrasenaPlana, boolean activo) {
        Usuario u = new Usuario();
        u.setNombreUsuario("oscar");
        u.setContrasena(encriptador.encode(contrasenaPlana));
        u.setRol("USUARIO");
        u.setActivo(activo);
        return u;
    }

    @Test
    void registrar_guardaLaContrasenaCifradaYElRolPorDefecto() {
        when(usuarioRepository.existsByNombreUsuario("oscar")).thenReturn(false);

        servicio.registrar(registro("oscar", "Clave1234", null));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();
        assertEquals("oscar", guardado.getNombreUsuario());
        assertNotEquals("Clave1234", guardado.getContrasena());          // nunca en texto plano
        assertTrue(guardado.getContrasena().startsWith("$2"));           // formato BCrypt
        assertTrue(encriptador.matches("Clave1234", guardado.getContrasena()));
        assertEquals("USUARIO", guardado.getRol());
        assertEquals(Boolean.TRUE, guardado.getActivo());
    }

    @Test
    void registrar_respetaElRolEnviado() {
        when(usuarioRepository.existsByNombreUsuario("jefe")).thenReturn(false);

        servicio.registrar(registro("jefe", "Clave1234", "ADMIN"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("ADMIN", captor.getValue().getRol());
    }

    @Test
    void registrar_rechazaUnUsuarioRepetido() {
        when(usuarioRepository.existsByNombreUsuario("oscar")).thenReturn(true);

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> servicio.registrar(registro("oscar", "Clave1234", null)));

        assertEquals("El usuario ya existe", error.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void autenticar_aceptaLasCredencialesCorrectas() {
        when(usuarioRepository.findByNombreUsuario("oscar")).thenReturn(Optional.of(usuarioGuardado("Clave1234", true)));

        assertTrue(servicio.autenticar(credenciales("oscar", "Clave1234")));
    }

    @Test
    void autenticar_rechazaUnaContrasenaIncorrecta() {
        when(usuarioRepository.findByNombreUsuario("oscar")).thenReturn(Optional.of(usuarioGuardado("Clave1234", true)));

        assertFalse(servicio.autenticar(credenciales("oscar", "OtraClave99")));
    }

    @Test
    void autenticar_rechazaUnUsuarioDesactivadoAunqueLaContrasenaSeaCorrecta() {
        when(usuarioRepository.findByNombreUsuario("oscar")).thenReturn(Optional.of(usuarioGuardado("Clave1234", false)));

        assertFalse(servicio.autenticar(credenciales("oscar", "Clave1234")));
    }

    @Test
    void autenticar_rechazaUnUsuarioQueNoExiste() {
        when(usuarioRepository.findByNombreUsuario("fantasma")).thenReturn(Optional.empty());

        assertFalse(servicio.autenticar(credenciales("fantasma", "Clave1234")));
    }
}
