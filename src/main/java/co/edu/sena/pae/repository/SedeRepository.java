package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del recurso sede: acceso a la tabla "sede".
 * Spring genera las consultas SQL a partir del nombre de cada metodo.
 */
public interface SedeRepository extends JpaRepository<Sede, Integer> {


}
