package server.handlers;

import model.RegisterResult;
import spark.Request;
import spark.Response;
import service.register;


public class RegisterHandler {

    public RegisterHandler() {

    }

    public RegisterResult handleRequest(Request req, Response res) {
        new register(req,res);
    }


}
