package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del recurso bodega: acceso a la tabla "bodega".
 * Spring genera las consultas SQL a partir del nombre de cada metodo.
 */
public interface BodegaRepository extends JpaRepository<Bodega, Integer> {


}
