package co.edu.sena.pae.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Llave primaria compuesta de la tabla detalle_entrada: (idEntrada, idLote).
 */
public class DetalleEntradaId implements Serializable {

    private Integer idEntrada;
    private Integer idLote;

    public DetalleEntradaId() {
    }

    public DetalleEntradaId(Integer idEntrada, Integer idLote) {
        this.idEntrada = idEntrada;
        this.idLote = idLote;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DetalleEntradaId otro = (DetalleEntradaId) o;
        return Objects.equals(idEntrada, otro.idEntrada) && Objects.equals(idLote, otro.idLote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEntrada, idLote);
    }
}
