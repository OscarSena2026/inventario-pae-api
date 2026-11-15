package co.edu.sena.pae.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.sena.pae.dto.ProductoDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de ProductoService (HU-02 registrar, HU-05 consultar con filtros, HU-08 modificar).
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock private ProductoRepository repository;
    @InjectMocks private ProductoService servicio;

    private ProductoDTO dto(String nombre, String categoria, String unidad, String perecedero, String minimo) {
        ProductoDTO d = new ProductoDTO();
        d.setNombreProducto(nombre);
        d.setCategoria(categoria);
        d.setUnidadMedida(unidad);
        d.setPerecedero(perecedero);
        d.setStockMinimo(new BigDecimal(minimo));
        return d;
    }

    private Producto producto(int id, String nombre) {
        Producto p = new Producto();
        p.setIdProducto(id);
        p.setNombreProducto(nombre);
        p.setCategoria("Granos");
        p.setUnidadMedida("kg");
        p.setPerecedero("NO");
        p.setStockMinimo(new BigDecimal("100"));
        return p;
    }

    @Test
    void listar_sinFiltrosDevuelveTodosLosProductos() {
        List<Producto> todos = List.of(producto(1, "Arroz"), producto(2, "Lentejas"));
        when(repository.findAll()).thenReturn(todos);

        assertSame(todos, servicio.listar(null, " "));
    }

    @Test
    void listar_conNombreYCategoriaUsaElFiltroCombinado() {
        List<Producto> filtrados = List.of(producto(1, "Arroz"));
        when(repository.findByNombreProductoContainingIgnoreCaseAndCategoriaIgnoreCase("arr", "Granos")).thenReturn(filtrados);

        assertSame(filtrados, servicio.listar("arr", "Granos"));
    }

    @Test
    void listar_soloConNombreFiltraPorNombre() {
        List<Producto> filtrados = List.of(producto(1, "Arroz"));
        when(repository.findByNombreProductoContainingIgnoreCase("arr")).thenReturn(filtrados);

        assertSame(filtrados, servicio.listar("arr", null));
    }

    @Test
    void listar_soloConCategoriaFiltraPorCategoria() {
        List<Producto> filtrados = List.of(producto(1, "Arroz"));
        when(repository.findByCategoriaIgnoreCase("Granos")).thenReturn(filtrados);

        assertSame(filtrados, servicio.listar("", "Granos"));
    }

    @Test
    void obtener_fallaSiElProductoNoExiste() {
        when(repository.findById(9)).thenReturn(Optional.empty());

        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(9));

        assertEquals("Producto con id 9 no existe", error.getMessage());
    }

    @Test
    void crear_copiaTodosLosCamposDelDtoALaEntidad() {
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto creado = servicio.crear(dto("Lentejas", "Granos", "kg", "NO", "20"));

        assertEquals("Lentejas", creado.getNombreProducto());
        assertEquals("Granos", creado.getCategoria());
        assertEquals("kg", creado.getUnidadMedida());
        assertEquals("NO", creado.getPerecedero());
        assertEquals(0, new BigDecimal("20").compareTo(creado.getStockMinimo()));
    }

    @Test
    void actualizar_modificaLosDatosDelProductoExistente() {
        Producto existente = producto(1, "Arroz");
        when(repository.findById(1)).thenReturn(Optional.of(existente));
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto actualizado = servicio.actualizar(1, dto("Arroz premium", "Granos", "libra", "NO", "250"));

        assertSame(existente, actualizado);
        assertEquals("Arroz premium", actualizado.getNombreProducto());
        assertEquals("libra", actualizado.getUnidadMedida());
        assertEquals(0, new BigDecimal("250").compareTo(actualizado.getStockMinimo()));
    }

    @Test
    void actualizar_fallaSiElProductoNoExiste() {
        when(repository.findById(7)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.actualizar(7, dto("X", "Y", "kg", "NO", "1")));

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        org.mockito.Mockito.verify(repository, org.mockito.Mockito.never()).save(captor.capture());
    }

    @Test
    void eliminar_borraYFuerzaLaValidacionDeLasLlavesForaneas() {
        Producto existente = producto(1, "Arroz");
        when(repository.findById(1)).thenReturn(Optional.of(existente));

        servicio.eliminar(1);

        InOrder orden = inOrder(repository);
        orden.verify(repository).delete(existente);
        orden.verify(repository).flush();
    }
}
