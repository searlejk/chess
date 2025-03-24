package service;

import dataaccess.DataAccess;
import exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    public DataAccess data;

    @BeforeEach
    void setUp() {
        try {
            DataAccessProvider.getDataAccess().clearGames();
            DataAccessProvider.getDataAccess().clearUsersAndAuth();
        } catch(Exception e){
            System.out.print("ClearGames or clearUsersAndAuth failed in test");
        }
        this.data = DataAccessProvider.getDataAccess();
    }

    @Test
    void clearUsersAndAuth() {
        try {
            data.addUser(new UserData("username", "password", "email"));
            data.addAuthData(new AuthData("1234","username"));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }

        try {
            data.clearUsersAndAuth();
        }catch(Exception e){
            System.out.print("ClearUsersAndAuth failed in test");
        }


        }


    @Test
    void registerDoubleRegisterThrowsException() {
        RegisterRequest req = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(req),
                "No Throw");

        assertThrows(DataAccessException.class, () -> UserService.register(req),
                "Yes Should Throw");
    }

    @Test
    void registerCorrectRegister() {
        RegisterRequest req = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(req),
                    "No Throw");
    }

    @Test
    void loginCorrectLogin() {
        RegisterRequest registerReq = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(registerReq),
                "No Throw");

        LoginRequest loginReq = new LoginRequest("username", "password");

        assertDoesNotThrow(() -> UserService.login(loginReq),
                "No Throw");
    }

    @Test
    void loginWrongCredentialsThrowsException() {
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
    void logoutCorrectLogout() {
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
    void logoutWrongLogoutThrowsException() {
        RegisterRequest registerReq = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> UserService.register(registerReq),
                "No Throw");

        LoginRequest loginReq = new LoginRequest("username", "password");
        String authToken = "12345";

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertThrows(exception.ServerResponseException.class, () -> UserService.logout(logoutRequest),
                "Yes Throw");
    }
}