package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.DetalleSalidaDTO;
import co.edu.sena.pae.dto.SalidaDTO;
import co.edu.sena.pae.dto.SalidaRespuestaDTO;
import co.edu.sena.pae.exception.ConflictoException;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.exception.ReglaNegocioException;
import co.edu.sena.pae.model.Bodega;
import co.edu.sena.pae.model.DetalleSalida;
import co.edu.sena.pae.model.Empleado;
import co.edu.sena.pae.model.Existencia;
import co.edu.sena.pae.model.Lote;
import co.edu.sena.pae.model.Salida;
import co.edu.sena.pae.model.Sede;
import co.edu.sena.pae.repository.BodegaRepository;
import co.edu.sena.pae.repository.DetalleSalidaRepository;
import co.edu.sena.pae.repository.EmpleadoRepository;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.LoteRepository;
import co.edu.sena.pae.repository.SalidaRepository;
import co.edu.sena.pae.repository.SedeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de las salidas de stock (HU-04).
 * Verifica que haya stock suficiente, guarda la salida con su detalle y descuenta la existencia.
 */
@Service
public class SalidaService {

    private final SalidaRepository salidaRepository;
    private final DetalleSalidaRepository detalleRepository;
    private final BodegaRepository bodegaRepository;
    private final SedeRepository sedeRepository;
    private final EmpleadoRepository empleadoRepository;
    private final LoteRepository loteRepository;
    private final ExistenciaRepository existenciaRepository;

    public SalidaService(SalidaRepository salidaRepository, DetalleSalidaRepository detalleRepository,
                         BodegaRepository bodegaRepository, SedeRepository sedeRepository,
                         EmpleadoRepository empleadoRepository, LoteRepository loteRepository,
                         ExistenciaRepository existenciaRepository) {
        this.salidaRepository = salidaRepository;
        this.detalleRepository = detalleRepository;
        this.bodegaRepository = bodegaRepository;
        this.sedeRepository = sedeRepository;
        this.empleadoRepository = empleadoRepository;
        this.loteRepository = loteRepository;
        this.existenciaRepository = existenciaRepository;
    }

    @Transactional
    public SalidaRespuestaDTO registrar(SalidaDTO dto) {
        // Regla de negocio: no se registran salidas con fecha futura
        if (dto.getFechaSalida().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de salida no puede ser futura");
        }
        Bodega bodega = bodegaRepository.findById(dto.getIdBodega()).orElseThrow(
                () -> new RecursoNoEncontradoException("Bodega con id " + dto.getIdBodega() + " no existe"));
        Sede sede = sedeRepository.findById(dto.getIdSede()).orElseThrow(
                () -> new RecursoNoEncontradoException("Sede con id " + dto.getIdSede() + " no existe"));
        Empleado empleado = empleadoRepository.findById(dto.getIdEmpleado()).orElseThrow(
                () -> new RecursoNoEncontradoException("Empleado con id " + dto.getIdEmpleado() + " no existe"));

        // Valida TODOS los productos antes de descontar: sin repetir, lote vigente y stock suficiente
        Set<Integer> lotesVistos = new HashSet<>();
        List<Existencia> existencias = new ArrayList<>();
        for (DetalleSalidaDTO d : dto.getDetalles()) {
            if (!lotesVistos.add(d.getIdLote())) {
                throw new ReglaNegocioException("El lote " + d.getIdLote() + " está repetido en la salida");
            }
            Lote lote = loteRepository.findById(d.getIdLote()).orElseThrow(
                    () -> new RecursoNoEncontradoException("Lote con id " + d.getIdLote() + " no existe"));
            if (lote.getFechaVencimiento().isBefore(dto.getFechaSalida())) {
                throw new ReglaNegocioException("El lote " + lote.getIdLote() + " está vencido y no puede despacharse");
            }
            Existencia existencia = existenciaRepository
                    .findByBodega_IdBodegaAndLote_IdLote(bodega.getIdBodega(), lote.getIdLote())
                    .orElseThrow(() -> new ConflictoException(
                            "Stock insuficiente: el lote " + lote.getIdLote() + " no tiene existencias en la bodega"));
            if (existencia.getCantidadDisponible().compareTo(d.getCantidad()) < 0) {
                throw new ConflictoException("Stock insuficiente: el lote " + lote.getIdLote()
                        + " tiene " + existencia.getCantidadDisponible().toPlainString()
                        + " disponible y se solicitan " + d.getCantidad().toPlainString());
            }
            existencias.add(existencia);
        }

        // Guarda la cabecera
        Salida salida = new Salida();
        salida.setFechaSalida(dto.getFechaSalida());
        salida.setMotivo(dto.getMotivo());
        salida.setBodega(bodega);
        salida.setSede(sede);
        salida.setEmpleado(empleado);
        Salida guardada = salidaRepository.save(salida);

        // Guarda cada detalle y descuenta la existencia
        for (int i = 0; i < dto.getDetalles().size(); i++) {
            DetalleSalidaDTO d = dto.getDetalles().get(i);
            Existencia existencia = existencias.get(i);

            DetalleSalida detalle = new DetalleSalida();
            detalle.setIdSalida(guardada.getIdSalida());
            detalle.setIdLote(existencia.getLote().getIdLote());
            detalle.setCantidad(d.getCantidad());
            detalleRepository.save(detalle);

            existencia.setCantidadDisponible(existencia.getCantidadDisponible().subtract(d.getCantidad()));
            existenciaRepository.save(existencia);
        }
        return convertir(guardada);
    }

    @Transactional(readOnly = true)
    public List<SalidaRespuestaDTO> listar() {
        return salidaRepository.findAll().stream().map(this::convertir).toList();
    }

    @Transactional(readOnly = true)
    public SalidaRespuestaDTO obtener(Integer id) {
        Salida salida = salidaRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Salida con id " + id + " no existe"));
        return convertir(salida);
    }

    // Convierte la salida y su detalle en una respuesta plana
    private SalidaRespuestaDTO convertir(Salida s) {
        SalidaRespuestaDTO r = new SalidaRespuestaDTO();
        r.setIdSalida(s.getIdSalida());
        r.setFechaSalida(s.getFechaSalida());
        r.setMotivo(s.getMotivo());
        r.setIdBodega(s.getBodega().getIdBodega());
        r.setNombreBodega(s.getBodega().getNombreBodega());
        r.setIdSede(s.getSede().getIdSede());
        r.setNombreSede(s.getSede().getNombreSede());
        r.setIdEmpleado(s.getEmpleado().getIdEmpleado());
        r.setNombresEmpleado(s.getEmpleado().getNombres());
        List<DetalleSalidaDTO> detalles = new ArrayList<>();
        for (DetalleSalida d : detalleRepository.findByIdSalida(s.getIdSalida())) {
            DetalleSalidaDTO dto = new DetalleSalidaDTO();
            dto.setIdLote(d.getIdLote());
            dto.setCantidad(d.getCantidad());
            detalles.add(dto);
        }
        r.setDetalles(detalles);
        return r;
    }
}
