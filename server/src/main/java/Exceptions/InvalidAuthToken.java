package Exceptions;

public class InvalidAuthToken extends DataAccessException {
    public InvalidAuthToken(String message) {
        super(message);
    }
}
