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