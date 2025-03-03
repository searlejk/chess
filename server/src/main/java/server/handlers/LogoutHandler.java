package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.LogoutRequest;
import model.EmptyResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    public String handleLogout(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());
//        for (String header : req.headers()) {
//            System.out.println(header + ": " + req.headers(header));
//        }

        String authToken = req.headers("Authorization");
        if (authToken == null) {
            res.status(401);
            return serializer.toJson(new EmptyResult());
        }
        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        EmptyResult emptyResult;

        try{
            emptyResult = UserService.logout(logoutRequest);
            res.status(200);
        }
        catch(DataAccessException e){
            res.status(400);
            emptyResult = new EmptyResult();
        }

        String answer = serializer.toJson(emptyResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
