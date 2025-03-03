package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataAccessProvider;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
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
    void register_DoubleRegister_throwsException() {
        RegisterRequest req = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(req),
                "No Throw");

        assertThrows(DataAccessException.class, () -> UserService.register(req),
                "Yes Should Throw");
    }

    @Test
    void register_CorrectRegister() {
        RegisterRequest req = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(req),
                    "No Throw");
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}