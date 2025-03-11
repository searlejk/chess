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
        try {
            DataAccessProvider.getDataAccess().clearGames();
            DataAccessProvider.getDataAccess().clearUsersAndAuth();
        }catch (Exception e){
            System.out.print("ClearGames failed in set up for test");
        }
        this.data = DataAccessProvider.getDataAccess();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void clearGames() {

//        try {
//            data.addGame(0, new GameData(1, "white", "black", "coolgame"));
//        } catch(exception.ResponseException e){
//
//        }
        ///data.clearGames();

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