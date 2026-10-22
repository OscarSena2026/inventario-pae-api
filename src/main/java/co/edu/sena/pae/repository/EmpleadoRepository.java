package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del recurso empleado: acceso a la tabla "empleado".
 * Spring genera las consultas SQL a partir del nombre de cada metodo.
 */
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {


}
