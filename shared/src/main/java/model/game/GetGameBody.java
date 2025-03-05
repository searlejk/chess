package model.game;

public record GetGameBody(
        chess.ChessGame.TeamColor playerColor,
        Integer gameID){
}