package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.game.GameData;
import model.user.AuthData;
import model.user.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {
    String username = "JosephSmith";
    String password = "BookofMormon123";
    String email = "prophetseerandrevelator@gmail.com";
    UserData user = new UserData(username,password,email);


    String authToken = UUID.randomUUID().toString();
    AuthData authData = new AuthData(authToken,username);
    AuthData nullAuthData = new AuthData(null,null);


    GameData gameData = new GameData (
            1,"","","gameName","chessGame");
    GameData nullGameData = new GameData (1,null,null,null,"chessGame");
    private Server server;



    @BeforeEach
    void setUp() {
        server = new Server();
        try{
            server.data.clearGames();
            server.data.clearUsersAndAuth();
        } catch(Exception e){
            System.out.print("MySqlDataAccessTest failed to clear in setUp");
        }
        server.run(8081);
    }

    @AfterEach
    void tearDown() {
        server.stop();
        try{
            server.data.clearGames();
            server.data.clearUsersAndAuth();
        } catch(Exception e){
            System.out.print("MySqlDataAccessTest failed to clear in tearDown");
        }
    }

    @Test
    void addUserCorrectly() {

        assertDoesNotThrow(() -> server.data.addUser(
                new UserData(username, password, email)),
                "No Throw");
        try {
            assertNotNull(server.data.getUser(username));
        } catch(Exception e){
            fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    void addUserRepeatUsernameThrowsException() {

        assertDoesNotThrow(() ->
                        server.data.addUser(new UserData(username, password, email)),
                "No Throw");

        assertThrows(DataAccessException.class, () ->
                        server.data.addUser(new UserData(username, password, email)),
                "This Should Throw Exception");
    }

    @Test
    void getUserCorrectly() {
        ///  add user
        assertDoesNotThrow(() ->
                        server.data.addUser(new UserData(username, password, email)),
                "No Throw");

        ///  check if user is there
        try {
            assertEquals(server.data.getUser(username).username(),user.username());
        } catch(Exception e){
            fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    void getUserNotFoundThrowsException() {
        ///  Does not add user

        ///  check if user is there
        try {
            assertNull(server.data.getUser(username));
        } catch (Exception e){
            fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    void addAuthDataCorrectly() {
        assertDoesNotThrow( () ->
                server.data.addAuthData(authData), "No Throw");
    }

    @Test
    void addAuthDataNullThrowException() {
        assertDoesNotThrow( () ->
                server.data.addAuthData(authData), "No Throw");

        assertThrows(exception.ResponseException.class, () ->
                server.data.addAuthData(nullAuthData), "This Should Throw");
    }

    @Test
    void getUserByAuthCorrectly() {
        assertDoesNotThrow( () -> server.data.addAuthData(authData), "No Throw");
        assertDoesNotThrow(() -> server.data.addUser(
                        new UserData(username, password, email)),
                "No Throw");

        assertDoesNotThrow(() -> server.data.getUserByAuth(authToken));
    }

    @Test
    void getUserByAuthNullThrowException() {
        assertDoesNotThrow( () -> server.data.addAuthData(authData), "No Throw");
        assertDoesNotThrow(() -> server.data.addUser(
                        new UserData(username, password, email)),
                "No Throw");

        assertThrows(exception.ResponseException.class, () ->
                server.data.getUserByAuth(null), "This Should Throw");
    }

    @Test
    void deleteAuthCorrectly() {
        assertDoesNotThrow( () ->
                server.data.addAuthData(authData), "No Throw");

        assertDoesNotThrow( () ->
                server.data.deleteAuth(authToken));
    }

    @Test
    void deleteAuthNullThrowsException() {
        assertThrows(exception.ResponseException.class, () ->
                server.data.deleteAuth(null));
    }

    @Test
    void listGamesCorrectly() {
        assertDoesNotThrow(() -> server.data.listGames());
    }

    @Test
    void addGameCorrectly() {
        assertDoesNotThrow(() ->
                server.data.addGame(1,gameData), "No Throw");
    }

    @Test
    void addGameNullGameNameThrowsException() {
        assertThrows(exception.ResponseException.class, () ->
                server.data.addGame(1,nullGameData), "This Should Throw");
    }

    @Test
    void getGameCorrectly() {
        assertDoesNotThrow(() ->
                server.data.addGame(1,gameData), "No Throw");

        assertDoesNotThrow(() ->
                server.data.getGame(1), "No Throw");
    }

    @Test
    void getGameNoGameReturnNull() {
        assertDoesNotThrow(() ->
                server.data.addGame(1,gameData), "No Throw");
        try{
        assertNull(server.data.getGame(-9));
        } catch(Exception e){
            fail("Exception: "+ e.getMessage());
        }
    }

    @Test
    void remGameCorrectly() {
        assertDoesNotThrow(() ->
                server.data.addGame(1,gameData), "No Throw");

        assertDoesNotThrow(() ->
                server.data.remGame(1), "No Throw");
    }

    @Test
    void remGameNullThrowException() {
        assertDoesNotThrow(() ->
                server.data.addGame(1,gameData), "No Throw");

        assertThrows(exception.ResponseException.class, () ->
                server.data.remGame(-9999), "No Throw");
    }

    @Test
    void checkPasswordCorrectPassword() {
        assertDoesNotThrow(() -> server.data.addUser(
                        new UserData(username, password, email)),
                "No Throw");
        try {
            assertTrue(
                    server.data.checkPassword(username, password));
        }catch(Exception e){
            fail("Exception: "+ e.getMessage());
        }
    }

    @Test
    void checkPasswordWrongPassword() {
        assertDoesNotThrow(() -> server.data.addUser(
                        new UserData(username, password, email)),
                "No Throw");
        try {
            assertFalse(
                    server.data.checkPassword(username, "lizard"));
        }catch(Exception e){
            fail("Exception: "+ e.getMessage());
        }
    }

    @Test
    void clearUsersAndAuth() {
        assertDoesNotThrow(() -> server.data.addUser(
                        new UserData(username, password, email)),
                "No Throw");

        assertDoesNotThrow( () ->
                server.data.addAuthData(authData), "No Throw");

        assertDoesNotThrow(() -> server.data.clearUsersAndAuth());

        assertThrows(DataAccessException.class, () ->
                server.data.getUserByAuth(authToken));
    }

    @Test
    void clearGames() {
        try {
            assertTrue(server.data.listGames().isEmpty());
        }catch(Exception e){
            fail("Exception: "+ e.getMessage());
        }

        try {
            server.data.addGame(1, gameData);
            server.data.addGame(2, gameData);
            server.data.addGame(3, gameData);
            server.data.addGame(4, gameData);
            server.data.clearGames();
        } catch(Exception e){
            fail("Exception: "+ e.getMessage());
        }

        try {
            assertTrue(server.data.listGames().isEmpty());
        }catch(Exception e){
            fail("Exception: "+ e.getMessage());
        }


    }

}