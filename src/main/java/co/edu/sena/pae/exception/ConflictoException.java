package co.edu.sena.pae.exception;

/**
 * Se lanza cuando el dato ya existe o hay conflicto con el estado actual (responde 409).
 */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
