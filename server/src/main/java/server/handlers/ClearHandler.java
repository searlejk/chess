package server.handlers;

import com.google.gson.Gson;
import model.other.EmptyResult;
import org.eclipse.jetty.util.HttpCookieStore;
import spark.Request;
import spark.Response;
import service.*;

public class ClearHandler {

    public String handleClear(Request req, Response res) {
        var serializer = new Gson();
        GameService.clearGames();
        UserService.clearUsersAndAuth();
        res.status(200);
        res.type("application/json");
        EmptyResult emptyResult = new EmptyResult();

        return serializer.toJson(emptyResult);
    }
}
