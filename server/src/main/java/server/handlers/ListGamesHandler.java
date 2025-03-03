package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.LogoutRequest;
import model.LogoutResult;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    public String handleListGames(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());
        for (String header : req.headers()) {
            System.out.println(header + ": " + req.headers(header));
        }

        String authToken = req.headers("Authorization");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        LogoutResult logoutResult;

        try{
            logoutResult = UserService.logout(logoutRequest);
            res.status(200);
        }
        catch(DataAccessException e){
            res.status(400);
            logoutResult = new LogoutResult();
        }

        String answer = serializer.toJson(logoutResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
