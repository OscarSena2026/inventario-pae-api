package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.CredencialesDTO;
import co.edu.sena.pae.dto.RegistroUsuarioDTO;
import co.edu.sena.pae.exception.ConflictoException;
import co.edu.sena.pae.model.Usuario;
import co.edu.sena.pae.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de registro e inicio de sesion (HU-01), reutilizada de la evidencia EV01.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    // Herramienta que protege (encripta) y compara contrasenas con BCrypt
    private final BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Registra un usuario nuevo; si el nombre ya existe responde 409
    @Transactional
    public void registrar(RegistroUsuarioDTO datos) {
        if (usuarioRepository.existsByNombreUsuario(datos.getUsuario())) {
            throw new ConflictoException("El usuario ya existe");
        }
        Usuario nuevo = new Usuario();
        nuevo.setNombreUsuario(datos.getUsuario());
        // La contrasena se guarda protegida, nunca en texto plano
        nuevo.setContrasena(encriptador.encode(datos.getContrasena()));
        nuevo.setRol(datos.getRol() == null || datos.getRol().isBlank() ? "USUARIO" : datos.getRol());
        nuevo.setActivo(true);
        usuarioRepository.save(nuevo);
    }

    // Verifica las credenciales: el usuario debe existir, estar activo y coincidir la contrasena
    @Transactional(readOnly = true)
    public boolean autenticar(CredencialesDTO datos) {
        return usuarioRepository.findByNombreUsuario(datos.getUsuario())
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .map(u -> encriptador.matches(datos.getContrasena(), u.getContrasena()))
                .orElse(false);
    }
}
