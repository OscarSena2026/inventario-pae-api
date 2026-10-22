package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.StockProductoDTO;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reportes del inventario (HU-09) y exportacion a CSV (HU-10).
 */
@Service
public class ReporteService {

    private final ProductoRepository productoRepository;
    private final ExistenciaRepository existenciaRepository;

    public ReporteService(ProductoRepository productoRepository, ExistenciaRepository existenciaRepository) {
        this.productoRepository = productoRepository;
        this.existenciaRepository = existenciaRepository;
    }

    // Stock actual de cada producto (opcionalmente filtrado por categoria)
    @Transactional(readOnly = true)
    public List<StockProductoDTO> stockPorProducto(String categoria) {
        List<Producto> productos = (categoria == null || categoria.isBlank())
                ? productoRepository.findAll()
                : productoRepository.findByCategoriaIgnoreCase(categoria);
        List<StockProductoDTO> filas = new ArrayList<>();
        for (Producto p : productos) {
            BigDecimal actual = existenciaRepository.stockTotal(p.getIdProducto());
            StockProductoDTO fila = new StockProductoDTO();
            fila.setIdProducto(p.getIdProducto());
            fila.setNombreProducto(p.getNombreProducto());
            fila.setCategoria(p.getCategoria());
            fila.setUnidadMedida(p.getUnidadMedida());
            fila.setStockActual(actual);
            fila.setStockMinimo(p.getStockMinimo());
            boolean critico = p.getStockMinimo() != null
                    && p.getStockMinimo().compareTo(BigDecimal.ZERO) > 0
                    && actual.compareTo(p.getStockMinimo()) <= 0;
            fila.setEstado(critico ? "CRITICO" : "NORMAL");
            filas.add(fila);
        }
        return filas;
    }

    // Genera el reporte en formato CSV con encabezados claros
    @Transactional(readOnly = true)
    public String stockPorProductoCsv(String categoria) {
        StringBuilder csv = new StringBuilder("idProducto,producto,categoria,unidadMedida,stockActual,stockMinimo,estado\n");
        for (StockProductoDTO f : stockPorProducto(categoria)) {
            csv.append(f.getIdProducto()).append(',')
                    .append(texto(f.getNombreProducto())).append(',')
                    .append(texto(f.getCategoria())).append(',')
                    .append(texto(f.getUnidadMedida())).append(',')
                    .append(f.getStockActual().toPlainString()).append(',')
                    .append(f.getStockMinimo() == null ? "" : f.getStockMinimo().toPlainString()).append(',')
                    .append(f.getEstado()).append('\n');
        }
        return csv.toString();
    }

    // Encierra el texto entre comillas para que las comas no rompan el CSV
    private String texto(String valor) {
        return "\"" + (valor == null ? "" : valor.replace("\"", "\"\"")) + "\"";
    }
}
