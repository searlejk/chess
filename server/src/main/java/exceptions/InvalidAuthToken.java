package exceptions;

public class InvalidAuthToken extends DataAccessException {
    public InvalidAuthToken(String message) {
        super(message);
    }
}
