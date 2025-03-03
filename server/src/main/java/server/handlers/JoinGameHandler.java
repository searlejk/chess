package server.handlers;

import Exceptions.IncorrectCredentialsException;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import Exceptions.DataAccessException;
import model.*;
import spark.Request;
import spark.Response;
import service.GameService;


public class JoinGameHandler {
    public String handleJoinGame(Request req, Response res) throws DataAccessException {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());
        ErrorResult errorResult;

        GetGameBody tempBody;
        try {
            tempBody = serializer.fromJson(req.body(), GetGameBody.class);
        } catch (JsonSyntaxException e) {
            res.status(400);
            return "Invalid request body";
        }

        int gameID = tempBody.gameID();
        String inputColor = tempBody.playerColor();
        if (inputColor == null ) {
            res.status(400);
            return "Invalid team color";
        }
        System.out.println("input color: " + inputColor);


        String authToken = req.headers("Authorization");
        ///  update string to uppercase
        String UpperColor = inputColor.toUpperCase();
        ChessGame.TeamColor teamColor = null;

        if (UpperColor.equals("WHITE")){
            teamColor = ChessGame.TeamColor.WHITE;
        }
        else if (UpperColor.equals("BLACK")){
            teamColor = ChessGame.TeamColor.BLACK;
        } else {
            res.status(400);
            throw new DataAccessException("Error: Invalid color type");
        }

        JoinRequest joinRequest = new JoinRequest(teamColor,gameID,authToken);

        EmptyResult getGameResult = null;

        try{
            GameService.joinGame(joinRequest);
            res.status(200);
        }
        catch(IncorrectCredentialsException e){
            res.status(400);
            errorResult = new ErrorResult("Error: incorrect gameID");
            return serializer.toJson(errorResult);
        }
        catch(DataAccessException | NullPointerException e){
            res.status(401);
            errorResult = new ErrorResult("Error: incorrect Color input");
            return serializer.toJson(errorResult);
        }

        String answer = serializer.toJson(getGameResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
