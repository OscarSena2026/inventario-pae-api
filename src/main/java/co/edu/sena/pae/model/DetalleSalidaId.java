package co.edu.sena.pae.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Llave primaria compuesta de la tabla detalle_salida: (idSalida, idLote).
 */
public class DetalleSalidaId implements Serializable {

    private Integer idSalida;
    private Integer idLote;

    public DetalleSalidaId() {
    }

    public DetalleSalidaId(Integer idSalida, Integer idLote) {
        this.idSalida = idSalida;
        this.idLote = idLote;
    }

    public Integer getIdSalida() {
        return idSalida;
    }

    public void setIdSalida(Integer idSalida) {
        this.idSalida = idSalida;
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
        DetalleSalidaId otro = (DetalleSalidaId) o;
        return Objects.equals(idSalida, otro.idSalida) && Objects.equals(idLote, otro.idLote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSalida, idLote);
    }
}
