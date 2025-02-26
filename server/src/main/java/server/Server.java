package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            return "post user";
        });

        Spark.post("/session", (req, res) -> {
            return "post session";
        });

        Spark.delete("/session", (req, res) -> {
            return "delete session";
        });

        Spark.get("/game", (req, res) -> {
            return "get game";
        });

        Spark.post("/game", (req, res) -> {
            return "post game";
        });

        Spark.put("/game", (req, res) -> {
            return "put game";
        });

        Spark.delete("/db", (req, res) -> {
            return "delete database (clear)";
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
