package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.AlertaStockDTO;
import co.edu.sena.pae.dto.AlertaVencimientoDTO;
import co.edu.sena.pae.exception.ReglaNegocioException;
import co.edu.sena.pae.model.Existencia;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.ProductoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Alertas automaticas del inventario (HU-06): stock minimo y proximos vencimientos.
 */
@Service
public class AlertaService {

    private final ProductoRepository productoRepository;
    private final ExistenciaRepository existenciaRepository;

    public AlertaService(ProductoRepository productoRepository, ExistenciaRepository existenciaRepository) {
        this.productoRepository = productoRepository;
        this.existenciaRepository = existenciaRepository;
    }

    // Productos cuyo stock total es menor o igual a su stock minimo
    @Transactional(readOnly = true)
    public List<AlertaStockDTO> stockMinimo() {
        List<AlertaStockDTO> alertas = new ArrayList<>();
        for (Producto p : productoRepository.findAll()) {
            BigDecimal minimo = p.getStockMinimo();
            if (minimo == null || minimo.compareTo(BigDecimal.ZERO) <= 0) {
                continue; // sin stock minimo definido no genera alerta
            }
            BigDecimal actual = existenciaRepository.stockTotal(p.getIdProducto());
            if (actual.compareTo(minimo) <= 0) {
                AlertaStockDTO a = new AlertaStockDTO();
                a.setIdProducto(p.getIdProducto());
                a.setNombreProducto(p.getNombreProducto());
                a.setStockActual(actual);
                a.setStockMinimo(minimo);
                a.setMensaje("El producto " + p.getNombreProducto() + " está en nivel crítico: "
                        + actual.toPlainString() + " disponible (mínimo " + minimo.toPlainString() + ")");
                alertas.add(a);
            }
        }
        return alertas;
    }

    // Lotes con existencias que vencen dentro de los proximos "dias" (o que ya vencieron)
    @Transactional(readOnly = true)
    public List<AlertaVencimientoDTO> vencimiento(int dias) {
        if (dias <= 0) {
            throw new ReglaNegocioException("El número de días debe ser mayor a cero");
        }
        LocalDate hoy = LocalDate.now();
        List<AlertaVencimientoDTO> alertas = new ArrayList<>();
        for (Existencia e : existenciaRepository.proximasAVencer(hoy.plusDays(dias))) {
            long restantes = ChronoUnit.DAYS.between(hoy, e.getLote().getFechaVencimiento());
            AlertaVencimientoDTO a = new AlertaVencimientoDTO();
            a.setIdLote(e.getLote().getIdLote());
            a.setIdProducto(e.getLote().getProducto().getIdProducto());
            a.setNombreProducto(e.getLote().getProducto().getNombreProducto());
            a.setIdBodega(e.getBodega().getIdBodega());
            a.setNombreBodega(e.getBodega().getNombreBodega());
            a.setCantidadDisponible(e.getCantidadDisponible());
            a.setFechaVencimiento(e.getLote().getFechaVencimiento());
            a.setDiasRestantes(restantes);
            a.setMensaje(restantes < 0
                    ? "Lote vencido hace " + (-restantes) + " días"
                    : "Lote vence en " + restantes + " días");
            alertas.add(a);
        }
        return alertas;
    }
}
