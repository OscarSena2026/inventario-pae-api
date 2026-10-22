package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.EmpleadoDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Empleado;
import co.edu.sena.pae.repository.EmpleadoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio del recurso empleado: consultar, crear, actualizar y eliminar.
 * Aplica las reglas del negocio y responde con excepciones que el manejador convierte en 404, 409 o 400.
 */
@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Empleado> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Empleado obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Empleado con id " + id + " no existe"));
    }

    @Transactional
    public Empleado crear(EmpleadoDTO dto) {
        Empleado entidad = new Empleado();
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public Empleado actualizar(Integer id, EmpleadoDTO dto) {
        Empleado entidad = obtener(id);
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        Empleado entidad = obtener(id);
        repository.delete(entidad);
        // flush fuerza la validacion de llaves foraneas: si tiene registros relacionados responde 409
        repository.flush();
    }

    // Copia los datos del DTO a la entidad
    private void aplicar(Empleado entidad, EmpleadoDTO dto) {
        entidad.setNombres(dto.getNombres());
        entidad.setCargo(dto.getCargo());
        entidad.setTelefono(dto.getTelefono());
    }
}
