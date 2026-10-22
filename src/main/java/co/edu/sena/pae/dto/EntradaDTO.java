package co.edu.sena.pae.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Datos para registrar una entrada de stock (HU-03): cabecera y lista de productos recibidos.
 */
public class EntradaDTO {

    @NotNull(message = "La fecha de entrada es obligatoria")
    private LocalDate fechaEntrada;

    @Size(max = 20, message = "El número de factura no puede superar 20 caracteres")
    private String numeroFactura;

    @NotNull(message = "La bodega es obligatoria")
    private Integer idBodega;

    @NotNull(message = "El proveedor es obligatorio")
    private Integer idProveedor;

    @NotNull(message = "El empleado es obligatorio")
    private Integer idEmpleado;

    @NotEmpty(message = "La entrada debe tener al menos un producto")
    @Valid
    private List<DetalleEntradaDTO> detalles;

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

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public List<DetalleEntradaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleEntradaDTO> detalles) {
        this.detalles = detalles;
    }

}
