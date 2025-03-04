package Exceptions;

public class IncorrectCredentialsException extends DataAccessException {
    public IncorrectCredentialsException(String message) {
        super(message);
    }
}
