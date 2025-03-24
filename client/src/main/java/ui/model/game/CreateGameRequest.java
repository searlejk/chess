package ui.model.game;

public record CreateGameRequest(
        String gameName,
        String authToken){
}