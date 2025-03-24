package ui.model.game;

public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        String game) {
}
