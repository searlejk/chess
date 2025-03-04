package server.handlers;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.other.ErrorResult;
import model.user.LoginRequest;
import model.user.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    public String handleLogin(Request req, Response res) {
        var serializer = new Gson();
        System.out.println("Received Request Body: " + req.body());
        LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = null;
        ErrorResult errorResult;



        try{
            loginResult = UserService.login(loginRequest);
            res.status(200);
        }
        catch(DataAccessException e){
            res.status(401);
            errorResult = new ErrorResult("Error: Incorrect Credentials");
            return serializer.toJson(errorResult);
        }

        String answer = serializer.toJson(loginResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }
}
