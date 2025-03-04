package model;

import chess.ChessGame;

import java.util.Collection;

public record ListGamesResult(
        Collection<GameData> games){
}