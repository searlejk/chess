package client;

import exceptions.DataAccessException;
import model.game.CreateGameRequest;
import model.game.CreateGameResult;
import model.game.ListGamesRequest;
import model.user.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.UserService;
import exceptions.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static RegisterRequest r = new RegisterRequest("username","password","email");
    private static LoginRequest l = new LoginRequest("username", "password");

    @BeforeEach
    public void init() {
        server = new Server();
        var serverUrl = "http://localhost:8081";
        facade = new ServerFacade(serverUrl);
        var port = server.run(8081);
        try {
            server.data.clearGames();
            server.data.clearUsersAndAuth();
        } catch (Exception e){
            System.out.println("@BeforeAll in ServerFacadeTests failed clearing data");
        }
        System.out.println("Started test HTTP server on " + port);

    }

    @AfterEach
    void stopServer() {
        server.stop();
        System.out.println("Server stopped.");
    }

    @Test
    void registerSuccess() {
        assertDoesNotThrow(() -> facade.register(r));

        assertDoesNotThrow(() -> server.data.getUser("username"));
    }

    @Test
    void registerNullUsername() {
        RegisterRequest rNull = new RegisterRequest(null,"password","email");
        assertThrows(exception.ResponseException.class, () -> facade.register(rNull));

        assertDoesNotThrow(() -> server.data.getUser("username"));
    }

    @Test
    void loginSuccess() {
        assertDoesNotThrow(() -> facade.register(r));

        try {
            assertEquals(facade.login(l).username(),"username");
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void loginInvalidCredentials() {
        assertDoesNotThrow(() -> facade.register(r));

        LoginRequest l = new LoginRequest("username", "WRONG_PASSWORD");
        try {
            assertThrows(Exception.class, () -> facade.login(l).username());
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void logoutSuccess() {
        try {
            facade.register(r);
            LoginResult lr = facade.login(l);

            LogoutRequest lo = new LogoutRequest(lr.authToken());
            assertDoesNotThrow(() -> facade.logout(lo));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void logoutWrongAuthToken() {
        try {
            facade.register(r);
            LoginResult lr = facade.login(l);

            LogoutRequest lo = new LogoutRequest("passwordauthToken");
            assertThrows(Exception.class, () -> facade.logout(lo));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void createSuccess() {
        try {
            facade.register(r);
            LoginResult lr = facade.login(l);

            CreateGameRequest c = new CreateGameRequest("name", lr.authToken());
            assertEquals(new CreateGameResult(1), facade.create(c));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void createGameBadAuthToken() {
        try {
            facade.register(r);

            CreateGameRequest c = new CreateGameRequest("game", "BAD_AUTH_TOKEN");
            assertThrows(Exception.class, () -> facade.create(c));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void listGamesSuccess() {
        try {
            facade.register(r);
            LoginResult lr = facade.login(l);

            CreateGameRequest c = new CreateGameRequest("name", lr.authToken());
            assertEquals(new CreateGameResult(1), facade.create(c));

            ListGamesRequest lgr = new ListGamesRequest(lr.authToken());
            facade.listGames(lgr);
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void listGamesBadAuthToken() {
        try {
            facade.register(r);
            LoginResult lr = facade.login(l);

            CreateGameRequest c = new CreateGameRequest("name", lr.authToken());
            assertEquals(new CreateGameResult(1), facade.create(c));

            ListGamesRequest lgr = new ListGamesRequest("BAD_AUTH_TOKEN");
            assertThrows(Exception.class, () -> facade.listGames(lgr));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void joinSuccess() {
        try {
            facade.register(r);
            LoginResult lr = facade.login(l);

            CreateGameRequest c = new CreateGameRequest("name", lr.authToken());
            assertEquals(new CreateGameResult(1), facade.create(c));

            JoinRequest jr = new JoinRequest("WHITE", 1,lr.authToken());
            facade.join(jr);
        } catch(Exception e){
            fail();
        }
    }

    @Test
    void joinBadAuthToken() {
        try {
            facade.register(r);
            LoginResult lr = facade.login(l);

            CreateGameRequest c = new CreateGameRequest("name", lr.authToken());
            assertEquals(new CreateGameResult(1), facade.create(c));

            JoinRequest jr = new JoinRequest("WHITE", 1,"BAD_AUTH_TOKEN");
            assertThrows(Exception.class, () -> facade.join(jr));
        } catch(Exception e){
            fail();
        }
    }

}
