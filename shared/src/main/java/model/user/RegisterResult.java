package model.user;

public record RegisterResult(
        String username,
        String authToken) {
}
