package server;

import dataaccess.DataAccess;
import dataaccess.DatabaseManager;
///import dataaccess.MySqlDataAccess;
import dataaccess.MySqlDataAccess;
import exceptions.DataAccessException;
import dataaccess.MemoryDataAccess;
import server.handlers.*;
import spark.*;

public class Server {
    public final MySqlDataAccess data;
    MySqlDataAccess dataAccess;

    public Server() {
        MySqlDataAccess temp;
        try {
            temp = new MySqlDataAccess();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MySqlDataAccess", e);
        }
        this.data = temp;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.exception(exception.ResponseException.class, (e, req, res) -> {
            res.status(e.statusCode);
            res.body(e.getMessage());
        });

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        /// actually going to handle my exceptions here as I should now


        // Register EndPoint
        Spark.post("/user", (req, res) -> {
            try {
                return (new RegisterHandler()).handleRequest(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // Login EndPoint
        Spark.post("/session", (req, res) -> {
            try {
                return (new LoginHandler()).handleLogin(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // Logout EndPoint
        Spark.delete("/session", (req, res) -> {
            try {
                return (new LogoutHandler()).handleLogout(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // ListGames EndPoint
        Spark.get("/game", (req, res) -> {
            try {
                return (new ListGamesHandler()).handleListGames(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // CreateGame EndPoint
        Spark.post("/game", (req, res) -> {
            try {
                return (new CreateGameHandler()).handleCreateGame(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // JoinGame EndPoint
        Spark.put("/game", (req, res) -> {
            try {
                return (new JoinGameHandler()).handleJoinGame(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // Clear EndPoint
        Spark.delete("/db", (req, res) -> {
            try {
                return (new ClearHandler()).handleClear(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });


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