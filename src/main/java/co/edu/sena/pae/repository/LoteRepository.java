package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del recurso lote: acceso a la tabla "lote".
 * Spring genera las consultas SQL a partir del nombre de cada metodo.
 */
public interface LoteRepository extends JpaRepository<Lote, Integer> {


}
