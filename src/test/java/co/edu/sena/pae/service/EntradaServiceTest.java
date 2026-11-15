package co.edu.sena.pae.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.sena.pae.dto.DetalleEntradaDTO;
import co.edu.sena.pae.dto.EntradaDTO;
import co.edu.sena.pae.dto.EntradaRespuestaDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.exception.ReglaNegocioException;
import co.edu.sena.pae.model.Bodega;
import co.edu.sena.pae.model.DetalleEntrada;
import co.edu.sena.pae.model.Empleado;
import co.edu.sena.pae.model.Entrada;
import co.edu.sena.pae.model.Existencia;
import co.edu.sena.pae.model.Lote;
import co.edu.sena.pae.model.Proveedor;
import co.edu.sena.pae.repository.BodegaRepository;
import co.edu.sena.pae.repository.DetalleEntradaRepository;
import co.edu.sena.pae.repository.EmpleadoRepository;
import co.edu.sena.pae.repository.EntradaRepository;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.LoteRepository;
import co.edu.sena.pae.repository.ProveedorRepository;
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
 * Pruebas unitarias de EntradaService (HU-03: registrar entrada y sumar stock).
 * Los repositorios se simulan con Mockito: no se necesita MySQL ni Spring.
 */
@ExtendWith(MockitoExtension.class)
class EntradaServiceTest {

    @Mock private EntradaRepository entradaRepository;
    @Mock private DetalleEntradaRepository detalleRepository;
    @Mock private BodegaRepository bodegaRepository;
    @Mock private ProveedorRepository proveedorRepository;
    @Mock private EmpleadoRepository empleadoRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private ExistenciaRepository existenciaRepository;
    @InjectMocks private EntradaService servicio;

    // ---------- datos de ejemplo ----------
    private Bodega bodega() {
        Bodega b = new Bodega();
        b.setIdBodega(1);
        b.setNombreBodega("Bodega Central");
        return b;
    }

    private Proveedor proveedor() {
        Proveedor p = new Proveedor();
        p.setIdProveedor(2);
        p.setNombreProveedor("Distribuidora La Sabana");
        return p;
    }

    private Empleado empleado() {
        Empleado e = new Empleado();
        e.setIdEmpleado(3);
        e.setNombres("Laura Martínez");
        return e;
    }

    private Lote lote(int id) {
        Lote l = new Lote();
        l.setIdLote(id);
        l.setFechaVencimiento(LocalDate.now().plusDays(90));
        return l;
    }

    private DetalleEntradaDTO detalle(int idLote, String cantidad) {
        DetalleEntradaDTO d = new DetalleEntradaDTO();
        d.setIdLote(idLote);
        d.setCantidad(new BigDecimal(cantidad));
        d.setValorUnitario(new BigDecimal("2500"));
        return d;
    }

    private EntradaDTO entrada(LocalDate fecha, DetalleEntradaDTO... detalles) {
        EntradaDTO dto = new EntradaDTO();
        dto.setFechaEntrada(fecha);
        dto.setNumeroFactura("F-001");
        dto.setIdBodega(1);
        dto.setIdProveedor(2);
        dto.setIdEmpleado(3);
        dto.setDetalles(List.of(detalles));
        return dto;
    }

    // Simula los tres catálogos que siempre se consultan primero.
    private void catalogosExisten() {
        when(bodegaRepository.findById(1)).thenReturn(Optional.of(bodega()));
        when(proveedorRepository.findById(2)).thenReturn(Optional.of(proveedor()));
        when(empleadoRepository.findById(3)).thenReturn(Optional.of(empleado()));
    }

    // Hace que save() devuelva la misma entrada con un id asignado, como haría la base de datos.
    private void guardarAsignaId() {
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> {
            Entrada e = inv.getArgument(0);
            e.setIdEntrada(5);
            return e;
        });
    }

    private void assertCantidad(String esperada, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperada).compareTo(real), "Cantidad esperada " + esperada + " pero fue " + real);
    }

    // ---------- pruebas ----------
    @Test
    void registrar_creaLaExistenciaCuandoElLoteNoEstabaEnLaBodega() {
        catalogosExisten();
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote(10)));
        guardarAsignaId();
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.empty());

        EntradaRespuestaDTO respuesta = servicio.registrar(entrada(LocalDate.now(), detalle(10, "100")));

        assertEquals(5, respuesta.getIdEntrada());
        assertEquals("Bodega Central", respuesta.getNombreBodega());
        ArgumentCaptor<Existencia> captor = ArgumentCaptor.forClass(Existencia.class);
        verify(existenciaRepository).save(captor.capture());
        assertCantidad("100", captor.getValue().getCantidadDisponible());
        verify(detalleRepository).save(any(DetalleEntrada.class));
    }

    @Test
    void registrar_sumaLaCantidadALaExistenciaQueYaTeniaLaBodega() {
        catalogosExisten();
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote(10)));
        guardarAsignaId();
        Existencia actual = new Existencia();
        actual.setBodega(bodega());
        actual.setLote(lote(10));
        actual.setCantidadDisponible(new BigDecimal("50"));
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.of(actual));

        servicio.registrar(entrada(LocalDate.now(), detalle(10, "30")));

        ArgumentCaptor<Existencia> captor = ArgumentCaptor.forClass(Existencia.class);
        verify(existenciaRepository).save(captor.capture());
        assertCantidad("80", captor.getValue().getCantidadDisponible());
    }

    @Test
    void registrar_conVariosProductosSumaCadaLoteALaBodega() {
        catalogosExisten();
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote(10)));
        when(loteRepository.findById(11)).thenReturn(Optional.of(lote(11)));
        guardarAsignaId();
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 10)).thenReturn(Optional.empty());
        when(existenciaRepository.findByBodega_IdBodegaAndLote_IdLote(1, 11)).thenReturn(Optional.empty());

        servicio.registrar(entrada(LocalDate.now(), detalle(10, "100"), detalle(11, "20")));

        ArgumentCaptor<Existencia> captor = ArgumentCaptor.forClass(Existencia.class);
        verify(existenciaRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        List<Existencia> guardadas = captor.getAllValues();
        assertCantidad("100", guardadas.get(0).getCantidadDisponible());
        assertCantidad("20", guardadas.get(1).getCantidadDisponible());
    }

    @Test
    void registrar_rechazaUnaFechaFutura() {
        EntradaDTO dto = entrada(LocalDate.now().plusDays(1), detalle(10, "10"));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> servicio.registrar(dto));

        assertTrue(error.getMessage().contains("futura"));
        verify(entradaRepository, never()).save(any(Entrada.class));
    }

    @Test
    void registrar_fallaSiLaBodegaNoExiste() {
        when(bodegaRepository.findById(1)).thenReturn(Optional.empty());

        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.registrar(entrada(LocalDate.now(), detalle(10, "10"))));

        assertEquals("Bodega con id 1 no existe", error.getMessage());
        verify(entradaRepository, never()).save(any(Entrada.class));
    }

    @Test
    void registrar_fallaSiElLoteNoExisteYNoGuardaNada() {
        catalogosExisten();
        when(loteRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.registrar(entrada(LocalDate.now(), detalle(10, "10"))));

        verify(entradaRepository, never()).save(any(Entrada.class));
        verify(existenciaRepository, never()).save(any(Existencia.class));
    }

    @Test
    void registrar_rechazaUnLoteRepetidoEnLaMismaEntrada() {
        catalogosExisten();
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote(10)));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(entrada(LocalDate.now(), detalle(10, "10"), detalle(10, "5"))));

        assertTrue(error.getMessage().contains("repetido"));
        verify(entradaRepository, never()).save(any(Entrada.class));
    }

    @Test
    void obtener_fallaSiLaEntradaNoExiste() {
        when(entradaRepository.findById(99)).thenReturn(Optional.empty());

        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(99));

        assertEquals("Entrada con id 99 no existe", error.getMessage());
    }
}
