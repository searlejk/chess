package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.handlers.ListGamesHandler;
import server.handlers.LoginHandler;
import server.handlers.LogoutHandler;
import server.handlers.RegisterHandler;
import spark.*;

public class Server {
    public final DataAccess data;
    DataAccess dataAccess = new MemoryDataAccess();


    public Server() {
        this.data = dataAccess;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // Register EndPoint
        Spark.post("/user", (req, res) -> (new RegisterHandler()).handleRequest(req,res));

        // Login EndPoint
        Spark.post("/session", (req, res) -> (new LoginHandler()).handleLogin(req,res));

        // Logout EndPoint
        Spark.delete("/session", (req, res) -> (new LogoutHandler()).handleLogout(req,res));

        // ListGames EndPoint
        Spark.get("/game", (req, res) -> (new ListGamesHandler()).handleListGames(req,res));

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}