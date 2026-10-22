package co.edu.sena.pae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente envia (JSON) para crear o actualizar el municipio.
 * Las anotaciones validan la informacion antes de llegar a la logica de negocio.
 */
public class MunicipioDTO {

    @NotBlank(message = "El nombre del municipio es obligatorio")
    @Size(max = 60, message = "El nombre del municipio no puede superar 60 caracteres")
    private String nombreMunicipio;

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

}
