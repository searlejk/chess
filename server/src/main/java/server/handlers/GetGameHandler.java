package server.handlers;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.game.GameData;
import model.other.ErrorResult;
import spark.Request;
import spark.Response;
import service.*;

public class GetGameHandler {

    public String handle(Request req, Response res) {
        System.out.println("\n*****[GetGameHandler]*****\n\n Request Body: \n" + req.body());
        var serializer = new Gson();
        String stringGameID = req.headers("gameid");
        int gameID = Integer.parseInt(stringGameID);
        System.out.print(gameID);
        GameData gameData;

        try {
            gameData = GameService.getGame(gameID);
            res.status(200);
            res.type("application/json");
            return serializer.toJson(gameData);
        } catch(DataAccessException e){
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: GetGameHandler Failed"));
        }

    }
}
