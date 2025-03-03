package service;

import dataaccess.DataAccess;
import Exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.*;
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
    void login_CorrectLogin() {
        RegisterRequest registerReq = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(registerReq),
                "No Throw");

        LoginRequest loginReq = new LoginRequest("username", "password");

        assertDoesNotThrow(() -> UserService.login(loginReq),
                "No Throw");
    }

    @Test
    void login_WrongCredentials_throwsException() {
        RegisterRequest registerReq = new RegisterRequest("username", "password", "email");
        LoginRequest loginReq = new LoginRequest("username", "password");
        LoginRequest wrongCredentials = new LoginRequest("username", "paasdfd");

        assertDoesNotThrow(() -> UserService.register(registerReq),
                "No Throw");


        assertDoesNotThrow(() -> UserService.login(loginReq),
                "No Throw");


        assertThrows(DataAccessException.class, () -> UserService.login(wrongCredentials));
    }

    @Test
    void logout_CorrectLogout() {
        RegisterRequest registerReq = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(registerReq),
                "No Throw");

        LoginRequest loginReq = new LoginRequest("username", "password");
        String authToken = "";

        try {
            LoginResult loginResult = UserService.login(loginReq);
            authToken = loginResult.authToken();
        } catch(DataAccessException e){

        }

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertDoesNotThrow(() -> UserService.logout(logoutRequest),
                "No Throw");
    }

    @Test
    void logout_WrongLogout_throwsException() {
        RegisterRequest registerReq = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(registerReq),
                "No Throw");

        LoginRequest loginReq = new LoginRequest("username", "password");
        String authToken = "12345";

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertThrows(NullPointerException.class, () -> UserService.logout(logoutRequest),
                "Yes Throw");
    }
}