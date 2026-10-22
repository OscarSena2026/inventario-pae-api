package co.edu.sena.pae.controller;

import co.edu.sena.pae.dto.StockProductoDTO;
import co.edu.sena.pae.service.ReporteService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reportes del inventario (/api/reportes) - HU-09 y HU-10.
 */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    // GET /api/reportes/stock-productos?categoria=Cereal -> 200 + stock actual por producto
    @GetMapping("/stock-productos")
    public List<StockProductoDTO> stockProductos(
            @RequestParam(name = "categoria", required = false) String categoria) {
        return service.stockPorProducto(categoria);
    }

    // GET /api/reportes/stock-productos/csv -> 200 + archivo CSV descargable
    @GetMapping("/stock-productos/csv")
    public ResponseEntity<String> stockProductosCsv(
            @RequestParam(name = "categoria", required = false) String categoria) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock_productos.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(service.stockPorProductoCsv(categoria));
    }
}
