package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.MunicipioDTO;
import co.edu.sena.pae.exception.ConflictoException;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Municipio;
import co.edu.sena.pae.repository.MunicipioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio del recurso municipio: consultar, crear, actualizar y eliminar.
 * Aplica las reglas del negocio y responde con excepciones que el manejador convierte en 404, 409 o 400.
 */
@Service
public class MunicipioService {

    private final MunicipioRepository repository;

    public MunicipioService(MunicipioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Municipio> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Municipio obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Municipio con id " + id + " no existe"));
    }

    @Transactional
    public Municipio crear(MunicipioDTO dto) {
        if (repository.findByNombreMunicipio(dto.getNombreMunicipio()).isPresent()) {
            throw new ConflictoException("Ya existe un municipio con ese nombre");
        }
        Municipio entidad = new Municipio();
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public Municipio actualizar(Integer id, MunicipioDTO dto) {
        Municipio entidad = obtener(id);
        if (repository.findByNombreMunicipio(dto.getNombreMunicipio())
                .filter(otro -> !otro.getIdMunicipio().equals(id)).isPresent()) {
            throw new ConflictoException("Ya existe un municipio con ese nombre");
        }
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        Municipio entidad = obtener(id);
        repository.delete(entidad);
        // flush fuerza la validacion de llaves foraneas: si tiene registros relacionados responde 409
        repository.flush();
    }

    // Copia los datos del DTO a la entidad
    private void aplicar(Municipio entidad, MunicipioDTO dto) {
        entidad.setNombreMunicipio(dto.getNombreMunicipio());
    }
}
