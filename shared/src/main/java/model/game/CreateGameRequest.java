package model.game;

public record CreateGameRequest(
        String gameName,
        String authToken){
}