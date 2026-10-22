package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.SedeDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Municipio;
import co.edu.sena.pae.model.Sede;
import co.edu.sena.pae.repository.MunicipioRepository;
import co.edu.sena.pae.repository.SedeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio del recurso sede: consultar, crear, actualizar y eliminar.
 * Aplica las reglas del negocio y responde con excepciones que el manejador convierte en 404, 409 o 400.
 */
@Service
public class SedeService {

    private final SedeRepository repository;
    private final MunicipioRepository municipioRepository;

    public SedeService(SedeRepository repository, MunicipioRepository municipioRepository) {
        this.repository = repository;
        this.municipioRepository = municipioRepository;
    }

    @Transactional(readOnly = true)
    public List<Sede> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Sede obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Sede con id " + id + " no existe"));
    }

    @Transactional
    public Sede crear(SedeDTO dto) {
        Sede entidad = new Sede();
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public Sede actualizar(Integer id, SedeDTO dto) {
        Sede entidad = obtener(id);
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        Sede entidad = obtener(id);
        repository.delete(entidad);
        // flush fuerza la validacion de llaves foraneas: si tiene registros relacionados responde 409
        repository.flush();
    }

    // Copia los datos del DTO a la entidad
    private void aplicar(Sede entidad, SedeDTO dto) {
        entidad.setNombreSede(dto.getNombreSede());
        entidad.setDireccion(dto.getDireccion());
        entidad.setMunicipio(buscarMunicipio(dto.getIdMunicipio()));
    }

    // Busca el municipio referenciado; si no existe responde 404
    private Municipio buscarMunicipio(Integer id) {
        return municipioRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Municipio con id " + id + " no existe"));
    }
}
