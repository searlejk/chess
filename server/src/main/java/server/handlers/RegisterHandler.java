package server.handlers;

import dataaccess.DataAccessException;
import model.RegisterRequest;
import model.RegisterResult;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;

public class RegisterHandler {

    public RegisterHandler() {

    }

    public String handleRequest(Request req, Response res) {
        var serializer = new Gson();

        System.out.println("Received Request Body: " + req.body());

        RegisterRequest registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);

        RegisterResult registerResult;

        try{
            registerResult = UserService.register(registerRequest);

        }
        catch(DataAccessException e){
            res.status(401);
            registerResult = new RegisterResult("username already taken",null);
        }

        if (registerResult.authToken() == null) {
            res.status(400);
        } else {
            res.status(200);
        }

        String answer = serializer.toJson(registerResult);
        System.out.println("Generated Response: " + answer);
        return answer;
    }


}
