package server.handlers;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.other.ErrorResult;
import model.user.LogoutRequest;
import model.other.EmptyResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    public String handle(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("\n*****[Logout]*****\n\n Request Body: \n" + req.body());
        String authToken = req.headers("Authorization");
        ErrorResult errorResult;
        EmptyResult emptyResult;

        try {
            UserService.checkAuthToken(authToken);
        } catch(DataAccessException | NullPointerException e){
            res.status(401);
            errorResult = new ErrorResult("Error: Invalid AuthToken");
            return serializer.toJson(errorResult);
        }

        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        try{
            emptyResult = UserService.logout(logoutRequest);
            res.status(200);
        }
        catch(DataAccessException e) {
            res.status(400);
            errorResult = new ErrorResult("Error: No user with authToken: "+ authToken);
            return serializer.toJson(errorResult);
        }

        String answer = serializer.toJson(emptyResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
