package server.handlers;

import exceptions.*;
import com.google.gson.Gson;
import model.game.GetGameBody;
import model.other.EmptyResult;
import model.other.ErrorResult;
import model.user.JoinRequest;
import service.UserService;
import spark.Request;
import spark.Response;
import service.GameService;


public class JoinGameHandler {
    public String handle(Request req, Response res) throws DataAccessException {
        var serializer = new Gson();
        System.out.println("\n*****[JoinGame]*****\n\n Request Body: \n" + req.body());
        String authToken = req.headers("Authorization");
        ErrorResult errorResult;

        // added to check before *******************
        UserService.checkAuthToken(authToken);
        System.out.println("authToken Good");


        GetGameBody tempBody;
        String inputColor;
        int gameID;

        try {
            tempBody = serializer.fromJson(req.body(), GetGameBody.class);
            gameID = tempBody.gameID();
            inputColor = tempBody.playerColor();
            System.out.println("input color: " + inputColor);
        }
        catch(NullPointerException e){
            res.status(400);
            errorResult = new ErrorResult("Error: null pointer exception");
            return serializer.toJson(errorResult);
        }

        ///  update string to uppercase
        //String upperCaseColor = inputColor.toUpperCase();

        JoinRequest joinRequest = new JoinRequest(inputColor,gameID,authToken);
        EmptyResult getGameResult;

        System.out.println("Entering joinGame Try Block");

        try{
            ///  ********************************************
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
            System.out.println("No Game Found Exception");
            return serializer.toJson(errorResult);
        }
        catch(InvalidAuthToken e){
            res.status(401);
            errorResult = new ErrorResult("Error: Invalid Auth Token");
            System.out.println("Invalid Auth Token");
            return serializer.toJson(errorResult);
        }
        catch(NullPointerException e){
            res.status(400);
            errorResult = new ErrorResult("Error: incorrect Color input");
            return serializer.toJson(errorResult);
        }
        catch(DataAccessException e){
            res.status(401);
            errorResult = new ErrorResult("Error: Data Access Exception");
            System.out.println("DataAccessException");
            return serializer.toJson(errorResult);

        }


        String answer = serializer.toJson(getGameResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
