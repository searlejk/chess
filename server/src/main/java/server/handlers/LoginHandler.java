package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    public String handleLogin(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());

        LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);

        LoginResult loginResult;

        try{
            loginResult = UserService.login(loginRequest);
        }
        catch(DataAccessException e){
            res.status(401);
            loginResult = new LoginResult("username already taken",null);
        }

        if (loginResult.authToken() == null) {
            res.status(400);
        } else {
            res.status(200);
        }

        String answer = serializer.toJson(loginResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
