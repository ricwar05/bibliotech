package pa.bibliotech.exception;

/** Se viola una regla del negocio (ISBN inválido, socio con préstamos...). */
public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) { super(message); }
}