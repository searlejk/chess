package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessProvider;
import dataaccess.MemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    public DataAccess data;

    @BeforeEach
    void setUp() {
        DataAccessProvider.dataAccess.clearGames();
        DataAccessProvider.dataAccess.clearUsersAndAuth();
        this.data = DataAccessProvider.dataAccess;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void clearGames() {

        data.addGame(1, new GameData(1,"white","black","coolgame",new ChessGame()));
        data.clearGames();

        assertTrue(data.listGames().isEmpty(), "Games should be empty after clear");

    }

    @Test
    void listGames() {
    }

    @Test
    void createGame() {
    }

    @Test
    void joinGame() {
    }
}