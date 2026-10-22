package co.edu.sena.pae.exception;

/**
 * Se lanza cuando se incumple una regla del negocio (responde 400).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
