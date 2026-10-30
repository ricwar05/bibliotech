package pa.bibliotech.exception;

//Envuelve SQLException: el servicio y la pantalla nunca ven SQLException//
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}