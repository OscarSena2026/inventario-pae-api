package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.ProductoDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio del recurso producto: consultar, crear, actualizar y eliminar.
 * Aplica las reglas del negocio y responde con excepciones que el manejador convierte en 404, 409 o 400.
 */
@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar(String nombre, String categoria) {
        boolean hayNombre = nombre != null && !nombre.isBlank();
        boolean hayCategoria = categoria != null && !categoria.isBlank();
        if (hayNombre && hayCategoria) {
            return repository.findByNombreProductoContainingIgnoreCaseAndCategoriaIgnoreCase(nombre, categoria);
        }
        if (hayNombre) {
            return repository.findByNombreProductoContainingIgnoreCase(nombre);
        }
        if (hayCategoria) {
            return repository.findByCategoriaIgnoreCase(categoria);
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Producto con id " + id + " no existe"));
    }

    @Transactional
    public Producto crear(ProductoDTO dto) {
        Producto entidad = new Producto();
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public Producto actualizar(Integer id, ProductoDTO dto) {
        Producto entidad = obtener(id);
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        Producto entidad = obtener(id);
        repository.delete(entidad);
        // flush fuerza la validacion de llaves foraneas: si tiene registros relacionados responde 409
        repository.flush();
    }

    // Copia los datos del DTO a la entidad
    private void aplicar(Producto entidad, ProductoDTO dto) {
        entidad.setNombreProducto(dto.getNombreProducto());
        entidad.setCategoria(dto.getCategoria());
        entidad.setUnidadMedida(dto.getUnidadMedida());
        entidad.setPerecedero(dto.getPerecedero());
        entidad.setStockMinimo(dto.getStockMinimo());
    }
}
