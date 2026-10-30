package pa.bibliotech.exception;

//Se busca algo que no existe.//
public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String message) { super(message); }
}