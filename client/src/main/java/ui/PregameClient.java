package ui;

import exception.ResponseException;
import model.user.LoginRequest;
import model.user.LoginResult;
import model.user.RegisterRequest;
import model.user.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;

public class PregameClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGEDOUT;

    public PregameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> logIn(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logIn(String... params) throws ResponseException {
        if (params.length >= 1 && params.length <= 2) {
            visitorName = params[0];

            ///  verify the users credentials
            LoginRequest loginRequest = new LoginRequest(params[0],params[1]);
            LoginResult loginResult;
            try {
                loginResult = server.login(loginRequest);
            } catch (Exception e) {
                return "Invalid Credentials, try again";
            }

            ///  Change state once the user's credentials are verified
            state = State.LOGGEDIN;
            return String.format("You signed in as %s.", loginResult.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            visitorName = String.join("-", params);


            ///  Register should not log the user in, but it should just add them to the database
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult;
            try {
                registerResult = server.register(registerRequest);
            } catch (Exception e){
                return "Username is already taken, try a new username";
            }

            return String.format("New Account Registered for: %s.", registerResult.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    \n
                    - help --> lists commands
                    - quit --> quits application
                    - login <username> <password> --> to play chess
                    - register <username> <password> <email> --> to create an account
                    """;
        }


        return """
                - signOut
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}