package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de usuarios: acceso a la tabla "usuarios".
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
