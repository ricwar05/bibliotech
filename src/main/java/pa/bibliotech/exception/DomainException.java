package pa.bibliotech.exception;

// Base de los errores del negocio: se muestran tal cual al usuario.//
public class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
}