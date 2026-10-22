package co.edu.sena.pae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente envia (JSON) para crear o actualizar el empleado.
 * Las anotaciones validan la informacion antes de llegar a la logica de negocio.
 */
public class EmpleadoDTO {

    @NotBlank(message = "El nombre del empleado es obligatorio")
    @Size(max = 100, message = "El nombre del empleado no puede superar 100 caracteres")
    private String nombres;

    @Size(max = 40, message = "El cargo no puede superar 40 caracteres")
    private String cargo;

    @Size(max = 15, message = "El teléfono no puede superar 15 caracteres")
    private String telefono;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
