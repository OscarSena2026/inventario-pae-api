package co.edu.sena.pae.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detalle de una entrada: lote recibido, cantidad y valor unitario (llave compuesta id_entrada + id_lote).
 */
@Entity
@Table(name = "detalle_entrada")
@IdClass(DetalleEntradaId.class)
public class DetalleEntrada {

    @Id
    @Column(name = "id_entrada")
    private Integer idEntrada;

    @Id
    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    public Integer getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(Integer idEntrada) {
        this.idEntrada = idEntrada;
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

}
