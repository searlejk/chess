package server.handlers;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.game.CreateGameRequest;
import model.game.CreateGameResult;
import model.game.GameName;
import model.other.ErrorResult;
import service.UserService;
import spark.Request;
import spark.Response;
import service.GameService;

public class CreateGameHandler {
    public String handleCreateGame(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());

        GameName gameName = serializer.fromJson(req.body(), GameName.class);
        String trueGameName = gameName.gameName();
        String authToken = req.headers("Authorization");
        CreateGameRequest createGameRequest = new CreateGameRequest(trueGameName,authToken);
        ErrorResult errorResult;

        try {
            UserService.checkAuthToken(authToken);
        } catch(DataAccessException | NullPointerException e){
            res.status(401);
            errorResult = new ErrorResult("Error: Invalid AuthToken");
            return serializer.toJson(errorResult);
        }


        CreateGameResult createGameResult = null;

        try{
            createGameResult = GameService.createGame(createGameRequest);
            res.status(200);

        }
        catch(DataAccessException e){
            res.status(400);
            errorResult = new ErrorResult("Error: Creating Game Failed");
            return serializer.toJson(errorResult);
        }

        String answer = serializer.toJson(createGameResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
