package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.BodegaDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Bodega;
import co.edu.sena.pae.model.Municipio;
import co.edu.sena.pae.repository.BodegaRepository;
import co.edu.sena.pae.repository.MunicipioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio del recurso bodega: consultar, crear, actualizar y eliminar.
 * Aplica las reglas del negocio y responde con excepciones que el manejador convierte en 404, 409 o 400.
 */
@Service
public class BodegaService {

    private final BodegaRepository repository;
    private final MunicipioRepository municipioRepository;

    public BodegaService(BodegaRepository repository, MunicipioRepository municipioRepository) {
        this.repository = repository;
        this.municipioRepository = municipioRepository;
    }

    @Transactional(readOnly = true)
    public List<Bodega> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Bodega obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Bodega con id " + id + " no existe"));
    }

    @Transactional
    public Bodega crear(BodegaDTO dto) {
        Bodega entidad = new Bodega();
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public Bodega actualizar(Integer id, BodegaDTO dto) {
        Bodega entidad = obtener(id);
        aplicar(entidad, dto);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        Bodega entidad = obtener(id);
        repository.delete(entidad);
        // flush fuerza la validacion de llaves foraneas: si tiene registros relacionados responde 409
        repository.flush();
    }

    // Copia los datos del DTO a la entidad
    private void aplicar(Bodega entidad, BodegaDTO dto) {
        entidad.setNombreBodega(dto.getNombreBodega());
        entidad.setDireccion(dto.getDireccion());
        entidad.setMunicipio(buscarMunicipio(dto.getIdMunicipio()));
    }

    // Busca el municipio referenciado; si no existe responde 404
    private Municipio buscarMunicipio(Integer id) {
        return municipioRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Municipio con id " + id + " no existe"));
    }
}
