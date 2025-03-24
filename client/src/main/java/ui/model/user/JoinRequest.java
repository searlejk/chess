package ui.model.user;

public record JoinRequest(
        String playerColor,
        int gameID,
        String authToken){
}