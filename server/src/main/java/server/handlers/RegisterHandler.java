package server.handlers;

import exceptions.DataAccessException;
import model.other.ErrorResult;
import model.user.RegisterRequest;
import model.user.RegisterResult;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;

public class RegisterHandler {

    public RegisterHandler() {

    }

    public String handle(Request req, Response res) {
        var serializer = new Gson();

        System.out.println("\n*****[Register]*****\n\n Request Body: \n" + req.body());

        RegisterRequest registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);

        RegisterResult registerResult = null;
        ErrorResult errorResult = null;

        if (registerRequest.email()==null || registerRequest.username() == null || registerRequest.password()==null){
            res.status(400);
            errorResult = new ErrorResult("Error: 1 of 3 required fields were null");
            return serializer.toJson(errorResult);
        }

        try{
            registerResult = UserService.register(registerRequest);

        }
        catch(DataAccessException e){
            res.status(403);
            errorResult = new ErrorResult("Error: Username Unavailable");
        }
        catch(Exception e){
            res.status(401);
            errorResult = new ErrorResult("Error: UserService.register really broke");
        }

        if (res.status()!=403) {
            res.status(200);
        }

        String answer;
        if (errorResult!=null) {
            res.type("application/json");
            answer = serializer.toJson(errorResult);
            System.out.println("Error Generated Response: " + answer);
        } else{
            res.type("application/json");
            answer = serializer.toJson(registerResult);
            System.out.println("Generated Response: " + answer);
        }

        return answer;
    }


}
