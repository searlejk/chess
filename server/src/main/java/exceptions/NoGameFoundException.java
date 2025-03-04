package exceptions;

public class NoGameFoundException extends DataAccessException {
    public NoGameFoundException(String message) {
        super(message);
    }
}
