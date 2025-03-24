package ui.model.user;

public record RegisterRequest(
        String username,
        String password,
        String email) {
}
