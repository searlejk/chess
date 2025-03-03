package server.handlers;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.*;

public class ClearHandler {

    public String handleClear(Request req, Response res) {
        GameService.clearGames();
        UserService.clearUsersAndAuth();
        res.status(200);
        res.type("application/json");
        return "{}";
    }
}
