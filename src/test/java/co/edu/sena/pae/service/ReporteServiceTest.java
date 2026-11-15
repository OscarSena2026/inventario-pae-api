package co.edu.sena.pae.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.edu.sena.pae.dto.StockProductoDTO;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de ReporteService (HU-09 reporte de stock y HU-10 exportación a CSV).
 */
@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private ExistenciaRepository existenciaRepository;
    @InjectMocks private ReporteService servicio;

    private Producto producto(int id, String nombre, String categoria, String minimo) {
        Producto p = new Producto();
        p.setIdProducto(id);
        p.setNombreProducto(nombre);
        p.setCategoria(categoria);
        p.setUnidadMedida("kg");
        p.setStockMinimo(minimo == null ? null : new BigDecimal(minimo));
        return p;
    }

    @Test
    void stockPorProducto_marcaCriticoSoloLosQueEstanEnElMinimoOPorDebajo() {
        when(productoRepository.findAll()).thenReturn(List.of(
                producto(1, "Arroz", "Granos", "100"),     // stock 50  -> CRITICO
                producto(2, "Lentejas", "Granos", "10"),   // stock 80  -> NORMAL
                producto(3, "Sal", "Condimentos", null))); // sin mínimo -> NORMAL
        when(existenciaRepository.stockTotal(1)).thenReturn(new BigDecimal("50"));
        when(existenciaRepository.stockTotal(2)).thenReturn(new BigDecimal("80"));
        when(existenciaRepository.stockTotal(3)).thenReturn(BigDecimal.ZERO);

        List<StockProductoDTO> filas = servicio.stockPorProducto(null);

        assertEquals(3, filas.size());
        assertEquals("CRITICO", filas.get(0).getEstado());
        assertEquals("NORMAL", filas.get(1).getEstado());
        assertEquals("NORMAL", filas.get(2).getEstado());
    }

    @Test
    void stockPorProducto_conCategoriaSoloConsultaEsaCategoria() {
        when(productoRepository.findByCategoriaIgnoreCase("Granos")).thenReturn(List.of(producto(1, "Arroz", "Granos", "100")));
        when(existenciaRepository.stockTotal(1)).thenReturn(new BigDecimal("500"));

        List<StockProductoDTO> filas = servicio.stockPorProducto("Granos");

        assertEquals(1, filas.size());
        assertEquals("NORMAL", filas.get(0).getEstado());
    }

    @Test
    void stockPorProductoCsv_generaEncabezadoYEncierraLosTextosEntreComillas() {
        when(productoRepository.findAll()).thenReturn(List.of(producto(1, "Arroz, blanco", "Granos", "100")));
        when(existenciaRepository.stockTotal(1)).thenReturn(new BigDecimal("50"));

        String csv = servicio.stockPorProductoCsv(null);

        assertTrue(csv.startsWith("idProducto,producto,categoria,unidadMedida,stockActual,stockMinimo,estado\n"));
        // La coma dentro del nombre no rompe el CSV porque va entre comillas.
        assertTrue(csv.contains("1,\"Arroz, blanco\",\"Granos\",\"kg\",50,100,CRITICO\n"));
    }

    @Test
    void stockPorProductoCsv_dejaVacioElMinimoCuandoNoEstaDefinido() {
        when(productoRepository.findAll()).thenReturn(List.of(producto(3, "Sal", "Condimentos", null)));
        when(existenciaRepository.stockTotal(3)).thenReturn(BigDecimal.ZERO);

        String csv = servicio.stockPorProductoCsv(null);

        assertTrue(csv.contains("3,\"Sal\",\"Condimentos\",\"kg\",0,,NORMAL\n"));
    }
}
