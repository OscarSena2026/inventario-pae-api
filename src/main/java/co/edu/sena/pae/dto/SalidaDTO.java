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
 * Datos para registrar una salida de stock (HU-04): cabecera y lista de productos despachados.
 */
public class SalidaDTO {

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate fechaSalida;

    @Size(max = 100, message = "El motivo no puede superar 100 caracteres")
    private String motivo;

    @NotNull(message = "La bodega es obligatoria")
    private Integer idBodega;

    @NotNull(message = "La sede es obligatoria")
    private Integer idSede;

    @NotNull(message = "El empleado es obligatorio")
    private Integer idEmpleado;

    @NotEmpty(message = "La salida debe tener al menos un producto")
    @Valid
    private List<DetalleSalidaDTO> detalles;

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

    public Integer getIdSede() {
        return idSede;
    }

    public void setIdSede(Integer idSede) {
        this.idSede = idSede;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public List<DetalleSalidaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleSalidaDTO> detalles) {
        this.detalles = detalles;
    }

}
