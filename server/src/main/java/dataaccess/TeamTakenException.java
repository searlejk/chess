package Exceptions;

public class TeamTakenException extends DataAccessException {
    public TeamTakenException(String message) {
        super(message);
    }
}
