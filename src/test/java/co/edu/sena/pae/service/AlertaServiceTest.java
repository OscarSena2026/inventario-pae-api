package co.edu.sena.pae.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.edu.sena.pae.dto.AlertaStockDTO;
import co.edu.sena.pae.dto.AlertaVencimientoDTO;
import co.edu.sena.pae.exception.ReglaNegocioException;
import co.edu.sena.pae.model.Bodega;
import co.edu.sena.pae.model.Existencia;
import co.edu.sena.pae.model.Lote;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.ProductoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de AlertaService (HU-06: alertas de stock mínimo y de vencimiento).
 */
@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private ExistenciaRepository existenciaRepository;
    @InjectMocks private AlertaService servicio;

    // Producto con su stock mínimo (null = sin mínimo definido).
    private Producto producto(int id, String nombre, String minimo) {
        Producto p = new Producto();
        p.setIdProducto(id);
        p.setNombreProducto(nombre);
        p.setStockMinimo(minimo == null ? null : new BigDecimal(minimo));
        return p;
    }

    // Existencia de un lote que vence dentro de "dias" días (negativo = ya venció).
    private Existencia existenciaQueVence(int idLote, long dias) {
        Producto p = producto(1, "Leche", "10");
        Lote lote = new Lote();
        lote.setIdLote(idLote);
        lote.setProducto(p);
        lote.setFechaVencimiento(LocalDate.now().plusDays(dias));
        Bodega bodega = new Bodega();
        bodega.setIdBodega(1);
        bodega.setNombreBodega("Bodega Central");
        Existencia e = new Existencia();
        e.setLote(lote);
        e.setBodega(bodega);
        e.setCantidadDisponible(new BigDecimal("12"));
        return e;
    }

    @Test
    void stockMinimo_soloAlertaLosProductosPorDebajoDelMinimo() {
        when(productoRepository.findAll()).thenReturn(List.of(
                producto(1, "Arroz", "100"),      // stock 50  -> alerta
                producto(2, "Lentejas", "10"),    // stock 80  -> sin alerta
                producto(3, "Sal", null),         // sin mínimo -> se ignora
                producto(4, "Azúcar", "0")));     // mínimo 0   -> se ignora
        when(existenciaRepository.stockTotal(1)).thenReturn(new BigDecimal("50"));
        when(existenciaRepository.stockTotal(2)).thenReturn(new BigDecimal("80"));

        List<AlertaStockDTO> alertas = servicio.stockMinimo();

        assertEquals(1, alertas.size());
        assertEquals(1, alertas.get(0).getIdProducto());
        assertEquals("Arroz", alertas.get(0).getNombreProducto());
        assertTrue(alertas.get(0).getMensaje().contains("nivel crítico"));
    }

    @Test
    void stockMinimo_alertaTambienCuandoElStockEsIgualAlMinimo() {
        when(productoRepository.findAll()).thenReturn(List.of(producto(1, "Arroz", "100")));
        when(existenciaRepository.stockTotal(1)).thenReturn(new BigDecimal("100"));

        assertEquals(1, servicio.stockMinimo().size());
    }

    @Test
    void vencimiento_rechazaDiasIgualesOMenoresACero() {
        ReglaNegocioException cero = assertThrows(ReglaNegocioException.class, () -> servicio.vencimiento(0));
        assertThrows(ReglaNegocioException.class, () -> servicio.vencimiento(-5));

        assertTrue(cero.getMessage().contains("mayor a cero"));
        verifyNoInteractions(existenciaRepository);
    }

    @Test
    void vencimiento_calculaLosDiasRestantesYElMensaje() {
        when(existenciaRepository.proximasAVencer(org.mockito.ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(List.of(existenciaQueVence(1, 10), existenciaQueVence(2, -3)));

        List<AlertaVencimientoDTO> alertas = servicio.vencimiento(30);

        assertEquals(2, alertas.size());
        assertEquals(10L, alertas.get(0).getDiasRestantes());
        assertEquals("Lote vence en 10 días", alertas.get(0).getMensaje());
        assertEquals(-3L, alertas.get(1).getDiasRestantes());
        assertEquals("Lote vencido hace 3 días", alertas.get(1).getMensaje());
        assertEquals("Leche", alertas.get(0).getNombreProducto());
    }

    @Test
    void vencimiento_consultaLosLotesHastaLaFechaLimite() {
        when(existenciaRepository.proximasAVencer(org.mockito.ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(List.of());

        servicio.vencimiento(30);

        ArgumentCaptor<LocalDate> limite = ArgumentCaptor.forClass(LocalDate.class);
        verify(existenciaRepository).proximasAVencer(limite.capture());
        assertEquals(LocalDate.now().plusDays(30), limite.getValue());
    }
}
