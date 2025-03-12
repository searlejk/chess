package dataaccess;

import exceptions.DataAccessException;
import model.user.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {
    String username = "JosephSmith";
    String password = "BookofMormon123";
    String email = "prophetseerandrevelator@gmail.com";
    UserData user = new UserData(username,password,email);
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
        server.run(8080);
    }

    @AfterEach
    void tearDown() {
        try{
            server.data.clearGames();
            server.data.clearUsersAndAuth();
        } catch(Exception e){
            System.out.print("MySqlDataAccessTest failed to clear in tearDown");
        }
        server.stop();
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
    void addAuthData() {
    }

    @Test
    void getUserByAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void listGames() {
    }

    @Test
    void addGame() {
    }

    @Test
    void getGame() {
    }

    @Test
    void remGame() {
    }

    @Test
    void clearUsersAndAuth() {
    }

    @Test
    void clearGames() {
    }

    @Test
    void listUserDatas() {
    }

    @Test
    void listAuthDatas() {
    }

    @Test
    void checkPassword() {
    }
}