package server.handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import exceptions.DataAccessException;
import model.other.EmptyResult;
import model.other.ErrorResult;
import spark.Request;
import spark.Response;
import service.*;

public class GetGameHandler {

    public String handle(Request req, Response res) {
        System.out.println("\n*****[Clear]*****\n\n Request Body: \n" + req.body());
        var serializer = new Gson();
        String stringGameID = req.headers("gameid");
        int gameID = Integer.parseInt(stringGameID);
        System.out.print(gameID);

        try {
            ChessGame game = GameService.getGame(gameID);
            res.status(200);
            res.type("application/json");
            System.out.print(serializer.toJson(game));
            return serializer.toJson(game);
        } catch(DataAccessException e){
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: GetGameHandler Failed"));
        }

    }
}
