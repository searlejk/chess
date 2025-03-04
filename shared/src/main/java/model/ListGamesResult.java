package model;

import chess.ChessGame;

import java.util.Collection;
import java.util.HashMap;

public record ListGamesResult(
        Collection<GameData> games){
}