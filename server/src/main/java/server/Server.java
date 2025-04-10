package server;

import dataaccess.DataAccess;
///import dataaccess.MySqlDataAccess;
import dataaccess.DataAccessProvider;
import dataaccess.MySqlDataAccess;
import server.handlers.*;
import spark.*;
import websocket.WebSocketHandler;

public class Server {
    public final DataAccess data;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        MySqlDataAccess temp;
        try {
            temp = new MySqlDataAccess();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MySqlDataAccess", e);
        }
        this.data = temp;
        DataAccessProvider.setDataAccess(temp);
        webSocketHandler = new WebSocketHandler();

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.exception(exception.ServerResponseException.class, (e, req, res) -> {
            res.status(e.statusCode);
            res.body(e.getMessage());
        });

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        /// my new webSocket code
        Spark.webSocket("/ws", webSocketHandler);

        // Register EndPoint
        Spark.post("/user", (req, res) -> {
            try {
                return (new RegisterHandler()).handle(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // Login EndPoint
        Spark.post("/session", (req, res) -> {
            try {
                return (new LoginHandler()).handle(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // Logout EndPoint
        Spark.delete("/session", (req, res) -> {
            try {
                return (new LogoutHandler()).handle(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // ListGames EndPoint
        Spark.get("/game", (req, res) -> {
            try {
                return (new ListGamesHandler()).handle(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // CreateGame EndPoint
        Spark.post("/game", (req, res) -> {
            try {
                return (new CreateGameHandler()).handle(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // JoinGame EndPoint
        Spark.put("/game", (req, res) -> {
            try {
                return (new JoinGameHandler()).handle(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // Clear EndPoint
        Spark.delete("/db", (req, res) -> {
            try {
                return (new ClearHandler()).handle(req, res);
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        Spark.get("/chessgame", (req, res) -> {
            try{
                return (new GetGameHandler()).handle(req,res);
            } catch(Exception e){
                return e.getMessage();
            }
        });

        Spark.put("/chessgame", (req, res) -> {
            try{
                return (new UpdateGameHandler()).handle(req,res);
            } catch(Exception e){
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