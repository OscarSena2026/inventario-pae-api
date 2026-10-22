package co.edu.sena.pae.controller;

import co.edu.sena.pae.dto.AlertaStockDTO;
import co.edu.sena.pae.dto.AlertaVencimientoDTO;
import co.edu.sena.pae.service.AlertaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Alertas del inventario (/api/alertas) - HU-06.
 */
@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    // GET /api/alertas/stock-minimo -> 200 + productos en nivel critico
    @GetMapping("/stock-minimo")
    public List<AlertaStockDTO> stockMinimo() {
        return service.stockMinimo();
    }

    // GET /api/alertas/vencimiento?dias=30 -> 200 + lotes por vencer, 400 si dias <= 0
    @GetMapping("/vencimiento")
    public List<AlertaVencimientoDTO> vencimiento(@RequestParam(name = "dias", defaultValue = "30") int dias) {
        return service.vencimiento(dias);
    }
}
