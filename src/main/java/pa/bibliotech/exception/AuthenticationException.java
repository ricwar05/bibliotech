package pa.bibliotech.exception;

// Inicio de sesión fallido. //
public class AuthenticationException extends DomainException {
    public AuthenticationException(String message) { super(message); }
}