package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessProvider;
import model.game.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    public DataAccess data;

    @BeforeEach
    void setUp() {
        DataAccessProvider.DATA_ACCESS.clearGames();
        DataAccessProvider.DATA_ACCESS.clearUsersAndAuth();
        this.data = DataAccessProvider.DATA_ACCESS;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void clearGames() {

        data.addGame(0, new GameData(1,"white","black","coolgame"));
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