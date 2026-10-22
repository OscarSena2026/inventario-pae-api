package co.edu.sena.pae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Datos que el cliente envia (JSON) para crear o actualizar el producto.
 * Las anotaciones validan la informacion antes de llegar a la logica de negocio.
 */
public class ProductoDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 80, message = "El nombre del producto no puede superar 80 caracteres")
    private String nombreProducto;

    @NotBlank(message = "La categoría es obligatorio")
    @Size(max = 40, message = "La categoría no puede superar 40 caracteres")
    private String categoria;

    @NotBlank(message = "La unidad de medida es obligatorio")
    @Size(max = 15, message = "La unidad de medida no puede superar 15 caracteres")
    private String unidadMedida;

    @NotBlank(message = "El campo perecedero es obligatorio")
    @Size(max = 2, message = "El campo perecedero no puede superar 2 caracteres")
    @Pattern(regexp = "SI|NO", message = "Perecedero debe ser SI o NO")
    private String perecedero;

    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private BigDecimal stockMinimo;

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getPerecedero() {
        return perecedero;
    }

    public void setPerecedero(String perecedero) {
        this.perecedero = perecedero;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

}
