package co.edu.sena.pae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos que el cliente envia (JSON) para crear o actualizar el lote.
 * Las anotaciones validan la informacion antes de llegar a la logica de negocio.
 */
public class LoteDTO {

    private LocalDate fechaFabricacion;

    @NotNull(message = "La fecha de vencimiento es obligatorio")
    private LocalDate fechaVencimiento;

    @NotNull(message = "La cantidad recibida es obligatorio")
    @Positive(message = "La cantidad recibida debe ser mayor a cero")
    private BigDecimal cantidadRecibida;

    @NotNull(message = "El producto es obligatorio")
    private Integer idProducto;

    @NotNull(message = "El proveedor es obligatorio")
    private Integer idProveedor;

    public LocalDate getFechaFabricacion() {
        return fechaFabricacion;
    }

    public void setFechaFabricacion(LocalDate fechaFabricacion) {
        this.fechaFabricacion = fechaFabricacion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getCantidadRecibida() {
        return cantidadRecibida;
    }

    public void setCantidadRecibida(BigDecimal cantidadRecibida) {
        this.cantidadRecibida = cantidadRecibida;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

}
