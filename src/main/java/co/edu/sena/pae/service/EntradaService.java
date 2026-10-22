package co.edu.sena.pae.service;

import co.edu.sena.pae.dto.DetalleEntradaDTO;
import co.edu.sena.pae.dto.EntradaDTO;
import co.edu.sena.pae.dto.EntradaRespuestaDTO;
import co.edu.sena.pae.exception.RecursoNoEncontradoException;
import co.edu.sena.pae.exception.ReglaNegocioException;
import co.edu.sena.pae.model.Bodega;
import co.edu.sena.pae.model.DetalleEntrada;
import co.edu.sena.pae.model.Empleado;
import co.edu.sena.pae.model.Entrada;
import co.edu.sena.pae.model.Existencia;
import co.edu.sena.pae.model.Lote;
import co.edu.sena.pae.model.Proveedor;
import co.edu.sena.pae.repository.BodegaRepository;
import co.edu.sena.pae.repository.DetalleEntradaRepository;
import co.edu.sena.pae.repository.EmpleadoRepository;
import co.edu.sena.pae.repository.EntradaRepository;
import co.edu.sena.pae.repository.ExistenciaRepository;
import co.edu.sena.pae.repository.LoteRepository;
import co.edu.sena.pae.repository.ProveedorRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de las entradas de stock (HU-03).
 * Valida los datos, guarda la entrada con su detalle y suma la cantidad a la existencia de la bodega.
 */
@Service
public class EntradaService {

    private final EntradaRepository entradaRepository;
    private final DetalleEntradaRepository detalleRepository;
    private final BodegaRepository bodegaRepository;
    private final ProveedorRepository proveedorRepository;
    private final EmpleadoRepository empleadoRepository;
    private final LoteRepository loteRepository;
    private final ExistenciaRepository existenciaRepository;

    public EntradaService(EntradaRepository entradaRepository, DetalleEntradaRepository detalleRepository,
                          BodegaRepository bodegaRepository, ProveedorRepository proveedorRepository,
                          EmpleadoRepository empleadoRepository, LoteRepository loteRepository,
                          ExistenciaRepository existenciaRepository) {
        this.entradaRepository = entradaRepository;
        this.detalleRepository = detalleRepository;
        this.bodegaRepository = bodegaRepository;
        this.proveedorRepository = proveedorRepository;
        this.empleadoRepository = empleadoRepository;
        this.loteRepository = loteRepository;
        this.existenciaRepository = existenciaRepository;
    }

    @Transactional
    public EntradaRespuestaDTO registrar(EntradaDTO dto) {
        // Regla de negocio: no se registran entradas con fecha futura
        if (dto.getFechaEntrada().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de entrada no puede ser futura");
        }
        Bodega bodega = bodegaRepository.findById(dto.getIdBodega()).orElseThrow(
                () -> new RecursoNoEncontradoException("Bodega con id " + dto.getIdBodega() + " no existe"));
        Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor()).orElseThrow(
                () -> new RecursoNoEncontradoException("Proveedor con id " + dto.getIdProveedor() + " no existe"));
        Empleado empleado = empleadoRepository.findById(dto.getIdEmpleado()).orElseThrow(
                () -> new RecursoNoEncontradoException("Empleado con id " + dto.getIdEmpleado() + " no existe"));

        // Valida cada producto antes de guardar nada: lote existente y sin repetir
        Set<Integer> lotesVistos = new HashSet<>();
        List<Lote> lotes = new ArrayList<>();
        for (DetalleEntradaDTO d : dto.getDetalles()) {
            if (!lotesVistos.add(d.getIdLote())) {
                throw new ReglaNegocioException("El lote " + d.getIdLote() + " está repetido en la entrada");
            }
            lotes.add(loteRepository.findById(d.getIdLote()).orElseThrow(
                    () -> new RecursoNoEncontradoException("Lote con id " + d.getIdLote() + " no existe")));
        }

        // Guarda la cabecera
        Entrada entrada = new Entrada();
        entrada.setFechaEntrada(dto.getFechaEntrada());
        entrada.setNumeroFactura(dto.getNumeroFactura());
        entrada.setBodega(bodega);
        entrada.setProveedor(proveedor);
        entrada.setEmpleado(empleado);
        Entrada guardada = entradaRepository.save(entrada);

        // Guarda cada detalle y suma la cantidad a la existencia de la bodega
        for (int i = 0; i < dto.getDetalles().size(); i++) {
            DetalleEntradaDTO d = dto.getDetalles().get(i);
            Lote lote = lotes.get(i);

            DetalleEntrada detalle = new DetalleEntrada();
            detalle.setIdEntrada(guardada.getIdEntrada());
            detalle.setIdLote(lote.getIdLote());
            detalle.setCantidad(d.getCantidad());
            detalle.setValorUnitario(d.getValorUnitario());
            detalleRepository.save(detalle);

            Existencia existencia = existenciaRepository
                    .findByBodega_IdBodegaAndLote_IdLote(bodega.getIdBodega(), lote.getIdLote())
                    .orElse(null);
            if (existencia == null) {
                existencia = new Existencia();
                existencia.setBodega(bodega);
                existencia.setLote(lote);
                existencia.setCantidadDisponible(d.getCantidad());
            } else {
                existencia.setCantidadDisponible(existencia.getCantidadDisponible().add(d.getCantidad()));
            }
            existenciaRepository.save(existencia);
        }
        return convertir(guardada);
    }

    @Transactional(readOnly = true)
    public List<EntradaRespuestaDTO> listar() {
        return entradaRepository.findAll().stream().map(this::convertir).toList();
    }

    @Transactional(readOnly = true)
    public EntradaRespuestaDTO obtener(Integer id) {
        Entrada entrada = entradaRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Entrada con id " + id + " no existe"));
        return convertir(entrada);
    }

    // Convierte la entrada y su detalle en una respuesta plana
    private EntradaRespuestaDTO convertir(Entrada e) {
        EntradaRespuestaDTO r = new EntradaRespuestaDTO();
        r.setIdEntrada(e.getIdEntrada());
        r.setFechaEntrada(e.getFechaEntrada());
        r.setNumeroFactura(e.getNumeroFactura());
        r.setIdBodega(e.getBodega().getIdBodega());
        r.setNombreBodega(e.getBodega().getNombreBodega());
        r.setIdProveedor(e.getProveedor().getIdProveedor());
        r.setNombreProveedor(e.getProveedor().getNombreProveedor());
        r.setIdEmpleado(e.getEmpleado().getIdEmpleado());
        r.setNombresEmpleado(e.getEmpleado().getNombres());
        List<DetalleEntradaDTO> detalles = new ArrayList<>();
        for (DetalleEntrada d : detalleRepository.findByIdEntrada(e.getIdEntrada())) {
            DetalleEntradaDTO dto = new DetalleEntradaDTO();
            dto.setIdLote(d.getIdLote());
            dto.setCantidad(d.getCantidad());
            dto.setValorUnitario(d.getValorUnitario());
            detalles.add(dto);
        }
        r.setDetalles(detalles);
        return r;
    }
}
