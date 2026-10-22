package co.edu.sena.pae.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Respuesta con la salida registrada y sus productos.
 */
public class SalidaRespuestaDTO {

    private Integer idSalida;

    private LocalDate fechaSalida;

    private String motivo;

    private Integer idBodega;

    private String nombreBodega;

    private Integer idSede;

    private String nombreSede;

    private Integer idEmpleado;

    private String nombresEmpleado;

    private List<DetalleSalidaDTO> detalles;

    public Integer getIdSalida() {
        return idSalida;
    }

    public void setIdSalida(Integer idSalida) {
        this.idSalida = idSalida;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Integer getIdBodega() {
        return idBodega;
    }

    public void setIdBodega(Integer idBodega) {
        this.idBodega = idBodega;
    }

    public String getNombreBodega() {
        return nombreBodega;
    }

    public void setNombreBodega(String nombreBodega) {
        this.nombreBodega = nombreBodega;
    }

    public Integer getIdSede() {
        return idSede;
    }

    public void setIdSede(Integer idSede) {
        this.idSede = idSede;
    }

    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombresEmpleado() {
        return nombresEmpleado;
    }

    public void setNombresEmpleado(String nombresEmpleado) {
        this.nombresEmpleado = nombresEmpleado;
    }

    public List<DetalleSalidaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleSalidaDTO> detalles) {
        this.detalles = detalles;
    }

}
