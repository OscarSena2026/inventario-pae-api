package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Existencia;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio de existencias: consulta el stock por bodega, lote y producto.
 */
public interface ExistenciaRepository extends JpaRepository<Existencia, Integer> {

    // Existencia de un lote en una bodega
    Optional<Existencia> findByBodega_IdBodegaAndLote_IdLote(Integer idBodega, Integer idLote);

    // Todas las existencias de una bodega
    List<Existencia> findByBodega_IdBodega(Integer idBodega);

    // Suma de la cantidad disponible de todos los lotes de un producto
    @Query("SELECT SUM(e.cantidadDisponible) FROM Existencia e WHERE e.lote.producto.idProducto = :idProducto")
    BigDecimal totalPorProducto(@Param("idProducto") Integer idProducto);

    // Existencias con stock que vencen hasta la fecha limite
    @Query("SELECT e FROM Existencia e WHERE e.cantidadDisponible > 0 AND e.lote.fechaVencimiento <= :limite "
            + "ORDER BY e.lote.fechaVencimiento")
    List<Existencia> proximasAVencer(@Param("limite") LocalDate limite);

    // Stock total del producto (0 si no tiene existencias)
    default BigDecimal stockTotal(Integer idProducto) {
        BigDecimal total = totalPorProducto(idProducto);
        return total == null ? BigDecimal.ZERO : total;
    }
}
