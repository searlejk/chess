package model;

import chess.ChessGame;

public record JoinRequest(
        String playerColor,
        int gameID,
        String authToken){
}