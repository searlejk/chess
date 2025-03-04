package model.user;

public record LoginResult(
        String username,
        String authToken){
}