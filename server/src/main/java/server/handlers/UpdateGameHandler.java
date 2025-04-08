package server.handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import exceptions.DataAccessException;
import model.game.GameData;
import model.other.EmptyResult;
import model.other.*;
import model.user.LogoutRequest;
import spark.Request;
import spark.Response;
import service.*;


public class UpdateGameHandler {

    public String handle(Request req, Response res) {
        System.out.println("\n*****[UpdateGameHandler]*****\n\n Request Body: \n" + req.body());
        var serializer = new Gson();
        GameData gameData = serializer.fromJson(req.body(), GameData.class);
        System.out.println(gameData + "\n");
        String newStringGame = gameData.game();
        LogoutRequest updateGameResult = new LogoutRequest("Yay it worked");


        try {
            GameService.updateGame(gameData,newStringGame);
            res.status(200);
            return serializer.toJson(updateGameResult);
        } catch(DataAccessException e){
            res.status(400);
            return serializer.toJson(new ErrorResult("Error: UpdateGameHandler Failed"));
        }

    }
}
