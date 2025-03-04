package model.user;

public record LoginRequest(
        String username,
        String password){
}