package ui;

import exception.ResponseException;
import model.user.*;
import server.ServerFacade;

import java.util.Arrays;

public class LoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    public State state = State.LOGGEDIN;
    private final String authToken;

    public LoginClient(String serverUrl, String authToken) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
//                case "create" -> create(params);
//                case "join" -> join(params);
//                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {
        if (params.length == 0) {
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            try {
                server.logout(logoutRequest);
            } catch(Exception e){
                return "Logout Failed";
            }

            state = State.LOGGEDOUT;
        }
        return "You Logged out";
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            visitorName = String.join("-", params);


            ///  Register should not log the user in, but it should just add them to the database
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult;
            LoginRequest loginRequest = new LoginRequest(params[0],params[1]);
            LoginResult loginResult;

            try {
                registerResult = server.register(registerRequest);
            } catch (Exception e){
                return "Username is already taken, try a new username";
            }

            try {
                loginResult = server.login(loginRequest);
            } catch (Exception e) {
                return "Invalid Credentials, try again";
            }

            state = State.LOGGEDIN;
            return String.format("You signed in as %s.", registerResult.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
        return """
                \n
                \tcreate <NAME> - a game
                \tlist - games
                \tjoin <ID> [WHITE|BLACK] - a game
                \tobserve <ID> - a game
                \tlogout - when you are done
                \tquit - playing chess
                \thelp - with possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}