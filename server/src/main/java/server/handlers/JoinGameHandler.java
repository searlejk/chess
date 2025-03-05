package server.handlers;

import exceptions.IncorrectCredentialsException;
import exceptions.NoGameFoundException;
import exceptions.TeamTakenException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exceptions.DataAccessException;
import model.game.GetGameBody;
import model.other.EmptyResult;
import model.other.ErrorResult;
import model.user.JoinRequest;
import spark.Request;
import spark.Response;
import service.GameService;


public class JoinGameHandler {
    public String handleJoinGame(Request req, Response res) throws DataAccessException {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());
        String authToken = req.headers("Authorization");


        GetGameBody tempBody;
        tempBody = serializer.fromJson(req.body(), GetGameBody.class);

        int gameID = tempBody.gameID();
        String inputColor = tempBody.playerColor();
        System.out.println("input color: " + inputColor);

        ErrorResult errorResult;

        ///  update string to uppercase
        String upperCaseColor = inputColor.toUpperCase();


        JoinRequest joinRequest = new JoinRequest(upperCaseColor,gameID,authToken);
        EmptyResult getGameResult;

        try{
            getGameResult = GameService.joinGame(joinRequest);
            res.status(200);
        }
        catch(TeamTakenException e){
            res.status(403);
            errorResult = new ErrorResult("Error: Team taken");
            return serializer.toJson(errorResult);
        }
        catch(IncorrectCredentialsException e){
            res.status(400);
            errorResult = new ErrorResult("Error: incorrect gameID");
            return serializer.toJson(errorResult);
        }
        catch(NoGameFoundException e){
            res.status(401);
            errorResult = new ErrorResult("Error: no game found");
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
