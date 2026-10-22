package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Proveedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del recurso proveedor: acceso a la tabla "proveedor".
 * Spring genera las consultas SQL a partir del nombre de cada metodo.
 */
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    // Busca por nit para validar que no se repita
    Optional<Proveedor> findByNit(String nit);
}
