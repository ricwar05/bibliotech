package pa.bibliotech.exception;


public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String message) { super(message); }
}