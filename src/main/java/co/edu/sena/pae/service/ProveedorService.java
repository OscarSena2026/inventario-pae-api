package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.ProveedorDTO;
import co.edu.sena.pae.exception.ConflictoException;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Proveedor;
import co.edu.sena.pae.repository.ProveedorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio del recurso proveedor: consultar, crear, actualizar y eliminar.
 * Aplica las reglas del negocio y responde con excepciones que el manejador convierte en 404, 409 o 400.
 */
@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Proveedor> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Proveedor obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Proveedor con id " + id + " no existe"));
    }

    @Transactional
    public Proveedor crear(ProveedorDTO dto) {
        if (repository.findByNit(dto.getNit()).isPresent()) {
            throw new ConflictoException("Ya existe un proveedor con ese NIT");
        }
        Proveedor entidad = new Proveedor();
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public Proveedor actualizar(Integer id, ProveedorDTO dto) {
        Proveedor entidad = obtener(id);
        if (repository.findByNit(dto.getNit())
                .filter(otro -> !otro.getIdProveedor().equals(id)).isPresent()) {
            throw new ConflictoException("Ya existe un proveedor con ese NIT");
        }
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        Proveedor entidad = obtener(id);
        repository.delete(entidad);
        // flush fuerza la validacion de llaves foraneas: si tiene registros relacionados responde 409
        repository.flush();
    }

    // Copia los datos del DTO a la entidad
    private void aplicar(Proveedor entidad, ProveedorDTO dto) {
        entidad.setNombreProveedor(dto.getNombreProveedor());
        entidad.setNit(dto.getNit());
        entidad.setTelefono(dto.getTelefono());
        entidad.setDireccion(dto.getDireccion());
    }
}
