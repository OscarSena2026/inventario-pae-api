package co.edu.sena.pae.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Respuesta con la entrada registrada y sus productos.
 */
public class EntradaRespuestaDTO {

    private Integer idEntrada;

    private LocalDate fechaEntrada;

    private String numeroFactura;

    private Integer idBodega;

    private String nombreBodega;

    private Integer idProveedor;

    private String nombreProveedor;

    private Integer idEmpleado;

    private String nombresEmpleado;

    private List<DetalleEntradaDTO> detalles;

    public Integer getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(Integer idEntrada) {
        this.idEntrada = idEntrada;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
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

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
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

    public List<DetalleEntradaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleEntradaDTO> detalles) {
        this.detalles = detalles;
    }

}
