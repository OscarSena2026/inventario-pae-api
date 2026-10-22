package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.ExistenciaRespuestaDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.model.Existencia;
import co.edu.sena.pae.repository.BodegaRepository;
import co.edu.sena.pae.repository.ExistenciaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta del stock disponible (HU-05).
 */
@Service
public class ExistenciaService {

    private final ExistenciaRepository existenciaRepository;
    private final BodegaRepository bodegaRepository;

    public ExistenciaService(ExistenciaRepository existenciaRepository, BodegaRepository bodegaRepository) {
        this.existenciaRepository = existenciaRepository;
        this.bodegaRepository = bodegaRepository;
    }

    @Transactional(readOnly = true)
    public List<ExistenciaRespuestaDTO> listar() {
        return existenciaRepository.findAll().stream().map(this::convertir).toList();
    }

    // Existencias de una bodega; si la bodega no existe responde 404
    @Transactional(readOnly = true)
    public List<ExistenciaRespuestaDTO> listarPorBodega(Integer idBodega) {
        if (!bodegaRepository.existsById(idBodega)) {
            throw new RecursoNoEncontradoException("Bodega con id " + idBodega + " no existe");
        }
        return existenciaRepository.findByBodega_IdBodega(idBodega).stream().map(this::convertir).toList();
    }

    // Convierte la entidad en una respuesta plana
    private ExistenciaRespuestaDTO convertir(Existencia e) {
        ExistenciaRespuestaDTO dto = new ExistenciaRespuestaDTO();
        dto.setIdExistencia(e.getIdExistencia());
        dto.setIdBodega(e.getBodega().getIdBodega());
        dto.setNombreBodega(e.getBodega().getNombreBodega());
        dto.setIdLote(e.getLote().getIdLote());
        dto.setIdProducto(e.getLote().getProducto().getIdProducto());
        dto.setNombreProducto(e.getLote().getProducto().getNombreProducto());
        dto.setFechaVencimiento(e.getLote().getFechaVencimiento());
        dto.setCantidadDisponible(e.getCantidadDisponible());
        return dto;
    }
}
