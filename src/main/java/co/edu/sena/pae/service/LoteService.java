package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.LoteDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.exception.ReglaNegocioException;
import co.edu.sena.pae.model.Lote;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.model.Proveedor;
import co.edu.sena.pae.repository.LoteRepository;
import co.edu.sena.pae.repository.ProductoRepository;
import co.edu.sena.pae.repository.ProveedorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio del recurso lote: consultar, crear, actualizar y eliminar.
 * Aplica las reglas del negocio y responde con excepciones que el manejador convierte en 404, 409 o 400.
 */
@Service
public class LoteService {

    private final LoteRepository repository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    public LoteService(LoteRepository repository, ProductoRepository productoRepository, ProveedorRepository proveedorRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional(readOnly = true)
    public List<Lote> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Lote obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Lote con id " + id + " no existe"));
    }

    @Transactional
    public Lote crear(LoteDTO dto) {
        Lote entidad = new Lote();
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public Lote actualizar(Integer id, LoteDTO dto) {
        Lote entidad = obtener(id);
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        Lote entidad = obtener(id);
        repository.delete(entidad);
        // flush fuerza la validacion de llaves foraneas: si tiene registros relacionados responde 409
        repository.flush();
    }

    // Copia los datos del DTO a la entidad
    private void aplicar(Lote entidad, LoteDTO dto) {
        if (dto.getFechaFabricacion() != null
                && !dto.getFechaVencimiento().isAfter(dto.getFechaFabricacion())) {
            throw new ReglaNegocioException("La fecha de vencimiento debe ser posterior a la fecha de fabricación");
        }
        entidad.setFechaFabricacion(dto.getFechaFabricacion());
        entidad.setFechaVencimiento(dto.getFechaVencimiento());
        entidad.setCantidadRecibida(dto.getCantidadRecibida());
        entidad.setProducto(buscarProducto(dto.getIdProducto()));
        entidad.setProveedor(buscarProveedor(dto.getIdProveedor()));
    }

    // Busca el producto referenciado; si no existe responde 404
    private Producto buscarProducto(Integer id) {
        return productoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Producto con id " + id + " no existe"));
    }

    // Busca el proveedor referenciado; si no existe responde 404
    private Proveedor buscarProveedor(Integer id) {
        return proveedorRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Proveedor con id " + id + " no existe"));
    }
}
