package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.DetalleSalida;
import co.edu.sena.pae.model.DetalleSalidaId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del detalle de salidas: acceso a la tabla "detalle_salida".
 */
public interface DetalleSalidaRepository extends JpaRepository<DetalleSalida, DetalleSalidaId> {

    // Productos (lotes) de una salida
    List<DetalleSalida> findByIdSalida(Integer idSalida);
}
