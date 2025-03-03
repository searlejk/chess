package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    public DataAccess data;

    @BeforeEach
    void setUp() {
        // If you want a fresh in-memory DB each time:
        data = new MemoryDataAccess();
    }

    @AfterEach
    void tearDown() {
        this.data = new MemoryDataAccess();
    }

    @Test
    void clearUsersAndAuth() {
        try {
            data.addUser(new UserData("username", "password", "email"));
            data.addAuthData(new AuthData("1234","username"));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }

        data.clearUsersAndAuth();

        // Now verify that it’s empty
        assertTrue(data.listUserDatas().isEmpty(), "Users should be empty after clear");
        assertTrue(data.listAuthDatas().isEmpty(), "Auth data should be empty after clear");
    }


    @Test
    void makeAuthData() {
    }

    @Test
    void register() {
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}