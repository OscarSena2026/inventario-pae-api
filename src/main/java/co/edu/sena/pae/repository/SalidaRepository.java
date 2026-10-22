package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Salida;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de la cabecera de salidas: acceso a la tabla "salida".
 */
public interface SalidaRepository extends JpaRepository<Salida, Integer> {
}
