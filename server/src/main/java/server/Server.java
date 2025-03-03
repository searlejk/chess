package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataAccessProvider;
import dataaccess.MemoryDataAccess;
import server.handlers.*;
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

        // CreateGame EndPoint
        Spark.post("/game", (req, res) -> (new CreateGameHandler()).handleCreateGame(req,res));

        // CreateGame EndPoint
        Spark.put("/game", (req, res) -> (new JoinGameHandler()).handleJoinGame(req,res));

        // Clear EndPoint
        Spark.delete("/db", (req, res) -> (new ClearHandler()).handleClear(req,res));

        Spark.exception(DataAccessException.class, (ex, req, res) -> {
            res.status(400);
            res.body("{\"message\":\"" + ex.getMessage() + "\"}");
        });

        //This line initializes the server and can be removed once you have a functioning endpoint
        ///Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}