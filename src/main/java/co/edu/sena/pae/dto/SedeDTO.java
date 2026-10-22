package co.edu.sena.pae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente envia (JSON) para crear o actualizar la sede.
 * Las anotaciones validan la informacion antes de llegar a la logica de negocio.
 */
public class SedeDTO {

    @NotBlank(message = "El nombre de la sede es obligatorio")
    @Size(max = 100, message = "El nombre de la sede no puede superar 100 caracteres")
    private String nombreSede;

    @Size(max = 100, message = "La dirección no puede superar 100 caracteres")
    private String direccion;

    @NotNull(message = "El municipio es obligatorio")
    private Integer idMunicipio;

    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

}
