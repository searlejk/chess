package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import ui.exceptions.ResponseException;
import ui.model.game.*;
import ui.model.other.EmptyResult;
import ui.model.other.GetGameRequest;
import ui.model.user.*;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.*;

import static ui.EscapeSequences.*;

public class LoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    public State state = State.LOGGEDIN;
    private final String authToken;
    private List<Integer> orderedGameID;
    private int color;
    public int gameID;
    public WebSocketFacade ws;


    public LoginClient(String serverUrl, String authToken) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.color = 1;
        this.gameID = 0;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create" -> create(params);
                case "list" -> listGames(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
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
            return "";
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
                return SET_TEXT_COLOR_YELLOW + "Create Game Failed" + SET_TEXT_COLOR_WHITE;
            }

            return SET_TEXT_COLOR_BLUE + "New Game Created\n" + SET_TEXT_COLOR_WHITE + "Name: " + params[0];
        }
        throw new ResponseException(400, "Expected: " + SET_TEXT_COLOR_YELLOW + "<NAME>" + SET_TEXT_COLOR_WHITE);
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 0) {
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult;
            try {
                listGamesResult = server.listGames(listGamesRequest);
            } catch(Exception e){
                return SET_TEXT_COLOR_YELLOW + "List Games Failed" + SET_TEXT_COLOR_WHITE;
            }
            StringBuilder response = new StringBuilder(SET_TEXT_COLOR_BLUE + "List of Games:\n" + SET_TEXT_COLOR_WHITE);
            int i = 1;
            Collection<GameData> gamesShuffled = listGamesResult.games();
            Collections.shuffle((List<?>) gamesShuffled);
            List<Integer> orderedGameID = new ArrayList<>();

            for (GameData game : gamesShuffled){
                orderedGameID.add(game.gameID());
                response.append(i).append(". ");
                response.append(game.gameName());
                response.append("\n\tWhite: ");
                response.append(game.whiteUsername());
                response.append("\n\tBlack: ");
                response.append(game.blackUsername());
                response.append("\n\n");
                i+=1;
            }

            this.orderedGameID = orderedGameID;

            return response.toString();
        }
        throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "only type 'list' for list" + SET_TEXT_COLOR_WHITE);
    }

    public String join(String... params) throws ResponseException {
        var serializer = new Gson();
        if (params.length == 2) {
            try {
                int num = Integer.parseInt(params[0]);
            } catch(NumberFormatException e){
                throw new ResponseException(400, "Expected: " + SET_TEXT_COLOR_YELLOW  + "<ID>" + SET_TEXT_COLOR_WHITE +" [WHITE|BLACK]");
            }

            if (orderedGameID == null){
                throw new ResponseException(400, "Must call " + SET_TEXT_COLOR_YELLOW  + " list " + SET_TEXT_COLOR_WHITE + "before calling join");
            }

            String color = params[1].toUpperCase();

            if (!Objects.equals(color, "WHITE") & !Objects.equals(color, "BLACK")){
                throw new ResponseException(400, "Expected: <ID> " + SET_TEXT_COLOR_YELLOW  +
                                                    "[WHITE|BLACK]" + SET_TEXT_COLOR_WHITE);
            }

            int index = Integer.parseInt(params[0]) - 1;
            int gameID;
            try {
                gameID = this.orderedGameID.get(index);
            } catch(IndexOutOfBoundsException e){
                index+=1;
                throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "No game found with ID: " + index + SET_TEXT_COLOR_WHITE);
            }

            JoinRequest joinRequest = new JoinRequest(color, gameID, authToken);

            try {
                EmptyResult emptyResult = server.join(joinRequest);
            } catch(Exception e){
                return SET_TEXT_COLOR_YELLOW + color + ": Already Taken" + SET_TEXT_COLOR_WHITE;
            }

            this.gameID = gameID;
            String stringGameID = String.valueOf(this.gameID);
            GetGameRequest getGameRequest = new GetGameRequest(stringGameID,authToken);
            GameData gameData = server.getGame(getGameRequest);
            ChessGame game = serializer.fromJson(gameData.game(),ChessGame.class);
            DrawChessHelper draw = new DrawChessHelper(game);

            ServerMessageHandler playerHandler = new ServerMessageHandler() {
                @Override
                public void notify(ServerMessage message) {
                    System.out.println("\n" + message.getServerMessageType());

//                            + "\n" + SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "[" +
//                            EscapeSequences.SET_TEXT_COLOR_WHITE + "IN_GAME" + EscapeSequences.SET_TEXT_COLOR_BLUE + "]" +
//                            EscapeSequences.SET_TEXT_COLOR_WHITE + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC);
                }
            };


            String teamColor = null;
            if (params[1].equalsIgnoreCase("WHITE")){
                teamColor = "WHITE";
            }
            if (params[1].equalsIgnoreCase("BLACK")){
                teamColor = "BLACK";
            }



            try {
                WebSocketFacade ws = new WebSocketFacade(serverUrl, playerHandler);
                assert teamColor != null;
                ws.joinGame(authToken,gameID);
                this.ws = ws;

            } catch(ResponseException e){
                return "Websocket Connection Failed";
            }

            if (Objects.equals(teamColor, "WHITE")){
                state = State.INGAME1;
                draw.drawChessWhite(game, null, null);
            }
            if (Objects.equals(teamColor, "BLACK")){
                state = State.INGAME2;
                draw.drawChessBlack(game,null,null);
            }




            return "";
        }
        throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <ID> [WHITE|BLACK]" + SET_TEXT_COLOR_WHITE);
    }

    public String observe(String... params) throws ResponseException {
        try {
            int num = Integer.parseInt(params[0]);
        } catch(NumberFormatException e){
            throw new ResponseException(400, "Expected: " + SET_TEXT_COLOR_YELLOW  + "<ID> " + SET_TEXT_COLOR_WHITE);
        }

        if (orderedGameID == null){
            throw new ResponseException(400, "Must call " + SET_TEXT_COLOR_YELLOW  + " list " + SET_TEXT_COLOR_WHITE + "before calling observe");
        }

        if (params.length == 1) {
            int index = Integer.parseInt(params[0]) - 1;
            gameID = this.orderedGameID.get(index);

            ChessGame game = new ChessGame();

            DrawChessHelper draw = new DrawChessHelper(game);
            this.color = 1;
            draw.drawChessWhite(game, null, null);

            state = State.OBSERVING;

            return "";
            //return SET_TEXT_COLOR_BLUE + "Observing game" + SET_TEXT_COLOR_WHITE;
        }
        throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <ID> " + SET_TEXT_COLOR_WHITE);
    }


    public String help() {
        return  SET_TEXT_COLOR_WHITE + """
                \n
                \tcreate <NAME> - a game
                \tlist - games
                \tjoin <ID> [WHITE|BLACK] - a game
                \tobserve <ID> - a game
                \tlogout - when you are done""" + "\n\t" +
                SET_TEXT_COLOR_YELLOW + "quit" + SET_TEXT_COLOR_WHITE +
                " - playing chess\n" +
                "\thelp - with possible commands\n";
    }
}