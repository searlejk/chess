package ui;

import exception.ResponseException;
import model.game.*;
import model.user.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
                case "create" -> create(params);
                case "list" -> listGames(params);
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
            return "You Logged out";
        }
        throw new ResponseException(400, "only type 'logout' to logout");
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0],authToken);
            CreateGameResult createGameResult;
            try {
                createGameResult = server.create(createGameRequest);
            } catch(Exception e){
                return "Create Game Failed";
            }

            return "New Game Created\n GameID: " + createGameResult.gameID();
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 0) {
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult;
            try {
                listGamesResult = server.listGames(listGamesRequest);
            } catch(Exception e){
                return "List Games Failed";
            }
            StringBuilder response = new StringBuilder("List of Games:\n");
            int i = 1;
            Collection<GameData> gamesShuffled = listGamesResult.games();
            Collections.shuffle((List<?>) gamesShuffled);

            for (GameData game : gamesShuffled){
                response.append(i+". ");
                response.append(game.gameName());
                response.append("\n\tWhite: ");
                response.append(game.whiteUsername());
                response.append("\n\tBlack: ");
                response.append(game.blackUsername());
                response.append("\n\n");
                i+=1;
            }

            return response.toString();
        }
        throw new ResponseException(400, "only type 'list' for list");
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