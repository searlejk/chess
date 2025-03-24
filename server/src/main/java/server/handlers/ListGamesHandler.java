package server.handlers;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.game.ListGamesRequest;
import model.game.ListGamesResult;
import model.other.EmptyResult;
import model.other.ErrorResult;
import service.UserService;
import spark.Request;
import spark.Response;
import service.GameService;

public class ListGamesHandler {
    public String handle(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("\n*****[ListGames]*****\n\n Request Body: \n" + req.body());
        String authToken = req.headers("Authorization");

        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult;
        ErrorResult errorResult;
        EmptyResult emptyResult = new EmptyResult();

        try {
            UserService.checkAuthToken(authToken);
        } catch(DataAccessException | NullPointerException e){
            res.status(401);
            errorResult = new ErrorResult("Error: Invalid AuthToken");
            return serializer.toJson(errorResult);
        }

        try{
            listGamesResult = GameService.listGames(listGamesRequest);
            res.status(200);
        }
        catch(exception.ServerResponseException e){
            return serializer.toJson(emptyResult);
        }
        catch(DataAccessException | NullPointerException e){
            res.status(401);
            errorResult = new ErrorResult("Error: 1 of 3 required fields were null");
            return serializer.toJson(errorResult);
        }

        System.out.println(listGamesResult);
        String answer = serializer.toJson(listGamesResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
