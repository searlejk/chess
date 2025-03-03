package server.handlers;

import com.google.gson.Gson;
import Exceptions.DataAccessException;
import model.*;
import spark.Request;
import spark.Response;
import service.GameService;

public class ListGamesHandler {
    public String handleListGames(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());

        String authToken = req.headers("Authorization");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        ListGamesResult listGamesResult;

        try{
            listGamesResult = GameService.listGames(listGamesRequest);
            res.status(200);
        }
        catch(DataAccessException e){
            res.status(400);
            listGamesResult = new ListGamesResult(null);
        }

        String answer = serializer.toJson(listGamesResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
