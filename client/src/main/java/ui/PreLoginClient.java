package ui;

import ui.exceptions.ResponseException;
import ui.model.user.LoginRequest;
import ui.model.user.LoginResult;
import ui.model.user.RegisterRequest;
import ui.model.user.*;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    public State state = State.LOGGEDOUT;
    public String authToken = null;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.print(SET_TEXT_COLOR_WHITE);
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
        if (params.length == 2) {
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
            this.authToken = loginResult.authToken();
            state = State.LOGGEDIN;
            return String.format("You signed in as %s.", loginResult.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
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

            this.authToken = loginResult.authToken();
            state = State.LOGGEDIN;
            return String.format("You signed in as %s.", registerResult.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
        return "\n\thelp - with possible commands\n\t" +
                SET_TEXT_COLOR_YELLOW +
                "quit" + RESET_BG_COLOR + SET_TEXT_COLOR_WHITE +
                " - the program\n" +
                SET_TEXT_COLOR_GREEN +
                "\tlogin " + SET_TEXT_COLOR_WHITE +
                "<username> <password> - to play chess\n" +
                SET_TEXT_COLOR_BLUE +
                "\tregister " + SET_TEXT_COLOR_WHITE + """
                <username> <password> <email> - to create an account
                """;
        }
}