package pa.bibliotech.exception;


public class DataAccessException extends DomainException {
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}