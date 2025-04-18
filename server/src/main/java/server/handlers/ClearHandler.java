package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import exceptions.DataAccessException;
import model.other.EmptyResult;
import model.other.ErrorResult;
import spark.Request;
import spark.Response;
import service.*;

public class ClearHandler {

    public String handle(Request req, Response res) {
        System.out.println("\n*****[Clear]*****\n\n Request Body: \n" + req.body());
        var serializer = new Gson();
        try {
            GameService.clearGames();
            UserService.clearUsersAndAuth();
            res.status(200);
            res.type("application/json");
            EmptyResult emptyResult = new EmptyResult();
            return serializer.toJson(emptyResult);
        } catch(DataAccessException e){
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: Clear Failed"));
        }
    }
}
