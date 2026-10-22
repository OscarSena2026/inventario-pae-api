package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.DetalleEntrada;
import co.edu.sena.pae.model.DetalleEntradaId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del detalle de entradas: acceso a la tabla "detalle_entrada".
 */
public interface DetalleEntradaRepository extends JpaRepository<DetalleEntrada, DetalleEntradaId> {

    // Productos (lotes) de una entrada
    List<DetalleEntrada> findByIdEntrada(Integer idEntrada);
}
