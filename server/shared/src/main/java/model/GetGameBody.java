package model;

import chess.ChessGame;

public record GetGameBody(
        String playerColor,
        int gameID){
}