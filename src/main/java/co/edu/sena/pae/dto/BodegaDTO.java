package co.edu.sena.pae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente envia (JSON) para crear o actualizar la bodega.
 * Las anotaciones validan la informacion antes de llegar a la logica de negocio.
 */
public class BodegaDTO {

    @NotBlank(message = "El nombre de la bodega es obligatorio")
    @Size(max = 80, message = "El nombre de la bodega no puede superar 80 caracteres")
    private String nombreBodega;

    @Size(max = 100, message = "La dirección no puede superar 100 caracteres")
    private String direccion;

    @NotNull(message = "El municipio es obligatorio")
    private Integer idMunicipio;

    public String getNombreBodega() {
        return nombreBodega;
    }

    public void setNombreBodega(String nombreBodega) {
        this.nombreBodega = nombreBodega;
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
