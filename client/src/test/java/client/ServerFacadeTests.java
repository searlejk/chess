package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        try {
            server.data.clearGames();
            server.data.clearUsersAndAuth();
        } catch (Exception e){
            System.out.println("@BeforeAll in ServerFacadeTests failed clearing data");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
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

    @Test
    void create() {
    }

    @Test
    void listGames() {
    }

    @Test
    void join() {
    }

}
