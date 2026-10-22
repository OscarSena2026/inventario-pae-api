package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del recurso producto: acceso a la tabla "producto".
 * Spring genera las consultas SQL a partir del nombre de cada metodo.
 */
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Filtros de consulta (HU-05)
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByNombreProductoContainingIgnoreCaseAndCategoriaIgnoreCase(String nombre, String categoria);
}
