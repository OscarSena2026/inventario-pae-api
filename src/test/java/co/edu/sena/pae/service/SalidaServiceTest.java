package co.edu.sena.pae.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.sena.pae.dto.DetalleSalidaDTO;
import co.edu.sena.pae.dto.SalidaDTO;
import co.edu.sena.pae.dto.SalidaRespuestaDTO;
import co.edu.sena.pae.exception.ConflictoException;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.exception.ReglaNegocioException;
import co.edu.sena.pae.model.Bodega;
import co.edu.sena.pae.model.Empleado;
import co.edu.sena.pae.model.Existencia;
import co.edu.sena.pae.model.Lote;
import co.edu.sena.pae.model.Salida;
import co.edu.sena.pae.model.Sede;
import co.edu.sena.pae.repository.BodegaRepository;
import co.edu.sena.pae.repository.DetalleSalidaRepository;
import co.edu.sena.pae.repository.EmpleadoRepository;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.LoteRepository;
import co.edu.sena.pae.repository.SalidaRepository;
import co.edu.sena.pae.repository.SedeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de SalidaService (HU-04: registrar salida, validar y descontar stock).
 * Los repositorios se simulan con Mockito: no se necesita MySQL ni Spring.
 */
@ExtendWith(MockitoExtension.class)
class SalidaServiceTest {

    @Mock private SalidaRepository salidaRepository;
    @Mock private DetalleSalidaRepository detalleRepository;
    @Mock private BodegaRepository bodegaRepository;
    @Mock private SedeRepository sedeRepository;
    @Mock private EmpleadoRepository empleadoRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private ExistenciaRepository existenciaRepository;
    @InjectMocks private SalidaService servicio;

    // ---------- datos de ejemplo ----------
    private Bodega bodega() {
        Bodega b = new Bodega();
        b.setIdBodega(1);
        b.setNombreBodega("Bodega Central");
        return b;
    }

    private Sede sede() {
        Sede s = new Sede();
        s.setIdSede(2);
        s.setNombreSede("IE San José");
        return s;
    }

    private Empleado empleado() {
        Empleado e = new Empleado();
        e.setIdEmpleado(3);
        e.setNombres("Laura Martínez");
        return e;
    }

    private Lote lote(int id, LocalDate vencimiento) {
        Lote l = new Lote();
        l.setIdLote(id);
        l.setFechaVencimiento(vencimiento);
        return l;
    }

    private Existencia existencia(Lote lote, String cantidad) {
        Existencia e = new Existencia();
        e.setBodega(bodega());
        e.setLote(lote);
        e.setCantidadDisponible(new BigDecimal(cantidad));
        return e;
    }

    private DetalleSalidaDTO detalle(int idLote, String cantidad) {
        DetalleSalidaDTO d = new DetalleSalidaDTO();
        d.setIdLote(idLote);
        d.setCantidad(new BigDecimal(cantidad));
        return d;
    }

    private SalidaDTO salida(LocalDate fecha, DetalleSalidaDTO... detalles) {
        SalidaDTO dto = new SalidaDTO();
        dto.setFechaSalida(fecha);
        dto.setMotivo("Despacho semanal");
        dto.setIdBodega(1);
        dto.setIdSede(2);
        dto.setIdEmpleado(3);
        dto.setDetalles(List.of(detalles));
        return dto;
    }

    private void catalogosExisten() {
        when(bodegaRepository.findById(1)).thenReturn(Optional.of(bodega()));
        when(sedeRepository.findById(2)).thenReturn(Optional.of(sede()));
        when(empleadoRepository.findById(3)).thenReturn(Optional.of(empleado()));
    }

    private void assertCantidad(String esperada, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperada).compareTo(real), "Cantidad esperada " + esperada + " pero fue " + real);
    }

    // ---------- pruebas ----------
    @Test
    void registrar_descuentaLaCantidadDeLaExistencia() {
        catalogosExisten();
        Lote lote = lote(10, LocalDate.now().plusDays(30));
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.of(existencia(lote, "200")));
        when(salidaRepository.save(any(Salida.class))).thenAnswer(inv -> {
            Salida s = inv.getArgument(0);
            s.setIdSalida(8);
            return s;
        });

        SalidaRespuestaDTO respuesta = servicio.registrar(salida(LocalDate.now(), detalle(10, "40")));

        assertEquals(8, respuesta.getIdSalida());
        assertEquals("IE San José", respuesta.getNombreSede());
        ArgumentCaptor<Existencia> captor = ArgumentCaptor.forClass(Existencia.class);
        verify(existenciaRepository).save(captor.capture());
        assertCantidad("160", captor.getValue().getCantidadDisponible());
    }

    @Test
    void registrar_conStockInsuficienteRespondeConflictoYNoDescuentaNada() {
        catalogosExisten();
        Lote lote = lote(10, LocalDate.now().plusDays(30));
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.of(existencia(lote, "30")));

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> servicio.registrar(salida(LocalDate.now(), detalle(10, "50"))));

        assertTrue(error.getMessage().startsWith("Stock insuficiente"));
        verify(salidaRepository, never()).save(any(Salida.class));
        verify(existenciaRepository, never()).save(any(Existencia.class));
    }

    @Test
    void registrar_conUnSoloProductoSinStockNoDescuentaNingunoDeLaLista() {
        catalogosExisten();
        Lote lote10 = lote(10, LocalDate.now().plusDays(30));
        Lote lote11 = lote(11, LocalDate.now().plusDays(30));
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote10));
        when(loteRepository.findById(11)).thenReturn(Optional.of(lote11));
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.of(existencia(lote10, "200")));
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 11)).thenReturn(Optional.of(existencia(lote11, "5")));

        assertThrows(ConflictoException.class,
                () -> servicio.registrar(salida(LocalDate.now(), detalle(10, "40"), detalle(11, "10"))));

        // Todo se valida antes de descontar: ni siquiera el primer producto (que sí alcanzaba) se descuenta.
        verify(existenciaRepository, never()).save(any(Existencia.class));
        verify(salidaRepository, never()).save(any(Salida.class));
    }

    @Test
    void registrar_conLoteSinExistenciasEnLaBodegaRespondeConflicto() {
        catalogosExisten();
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote(10, LocalDate.now().plusDays(30))));
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.empty());

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> servicio.registrar(salida(LocalDate.now(), detalle(10, "5"))));

        assertTrue(error.getMessage().contains("no tiene existencias"));
    }

    @Test
    void registrar_rechazaUnLoteVencido() {
        catalogosExisten();
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote(10, LocalDate.now().minusDays(1))));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(salida(LocalDate.now(), detalle(10, "5"))));

        assertTrue(error.getMessage().contains("vencido"));
        verify(salidaRepository, never()).save(any(Salida.class));
    }

    @Test
    void registrar_rechazaUnaFechaFutura() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(salida(LocalDate.now().plusDays(1), detalle(10, "5"))));

        assertTrue(error.getMessage().contains("futura"));
        verify(salidaRepository, never()).save(any(Salida.class));
    }

    @Test
    void registrar_rechazaUnLoteRepetidoEnLaMismaSalida() {
        catalogosExisten();
        Lote lote = lote(10, LocalDate.now().plusDays(30));
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.of(existencia(lote, "200")));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(salida(LocalDate.now(), detalle(10, "5"), detalle(10, "5"))));

        assertTrue(error.getMessage().contains("repetido"));
        verify(existenciaRepository, never()).save(any(Existencia.class));
    }

    @Test
    void registrar_fallaSiLaSedeNoExiste() {
        when(bodegaRepository.findById(1)).thenReturn(Optional.of(bodega()));
        when(sedeRepository.findById(2)).thenReturn(Optional.empty());

        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.registrar(salida(LocalDate.now(), detalle(10, "5"))));

        assertEquals("Sede con id 2 no existe", error.getMessage());
    }
}
