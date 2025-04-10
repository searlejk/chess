package ui;

import chess.*;
import com.google.gson.Gson;
import ui.exceptions.ResponseException;
import ui.model.game.*;
import ui.model.other.GetGameRequest;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.net.http.WebSocket;
import java.util.*;

import static ui.EscapeSequences.*;

public class GameClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    public State state;
    private final String authToken;
    private List<Integer> orderedGameID;
    private int side;
    private Boolean gameOver = false;
    private Integer gameID;
    public Boolean observing;
    private final List<String> letters = Arrays.asList("a","b","c","d","e","f","g","h");
    private WebSocketFacade ws = null;
    private ChessGame tempGame;

    public GameClient(String serverUrl, String authToken, int side, int gameID) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.side = side; // 1 is white 2 is black, 3 is observing
        this.gameOver = false;
        this.gameID = gameID;
        this.observing = false;

        try {
            // Anonymous class is still here, but now 'notify' is just a one-liner:
            this.ws = new WebSocketFacade(serverUrl, new ServerMessageHandler() {
                @Override
                public void notify(ServerMessage message) {
                    handleServerMessage(message);
                }
            });
        } catch (Exception e) {
            System.out.print("Failed to connect WebSocket");
            state = State.LOGGEDIN;
        }
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.print(SET_TEXT_COLOR_WHITE);
            return switch (cmd) {
                case "move" -> move(params);
                case "resign" -> resign(params);
                case "redraw" -> redraw(params);
                case "leave" -> leave(params);
                case "legalmoves" -> legalMoves(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String legalMoves(String... params) throws ResponseException {
        if (params.length == 1) {
            String input = params[0];
            ChessPosition pos = parsePositionInput(input);
            DrawChessHelper draw = new DrawChessHelper(tempGame);
            draw.legalMoves(pos, side);
            return "";
        }
        else{
            throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <COORDINATE>" + SET_TEXT_COLOR_WHITE);
        }
    }

    public String redraw(String... params) throws ResponseException{
        System.out.print(ERASE_SCREEN);
        DrawChessHelper draw = new DrawChessHelper(tempGame);
        if (side==2) {
            draw.drawChess(tempGame,null,null, ChessGame.TeamColor.BLACK);
        }else{
            draw.drawChess(tempGame,null,null, ChessGame.TeamColor.WHITE);
        }
        return "";
    }

    public String move(String... params) throws ResponseException{
        if (params.length == 2) {

            ChessPosition startPos = parsePositionInput(params[0]);
            ChessPosition endPos = parsePositionInput(params[1]);
            ChessMove move = new ChessMove(startPos, endPos, null);

            ws.makeMove(authToken,gameID,move);
            return "";
        } else{
            throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <COORDINATE> <COORDINATE> " + SET_TEXT_COLOR_WHITE);
        }
    }

    public String resign(String... params){

        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you would like to resign? Y/N\n");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("y") | answer.equals("yes")){
            try {
                ws.resign(authToken, gameID);
            } catch(Exception e){
                return "resign failed";
            }
            System.out.print("");
        } else{
            System.out.print("Resignation cancelled");
        }
        return "";
    }

    public String leave(String... params) throws ResponseException {
        String leftGameMessage = SET_TEXT_COLOR_BLUE + "you left the game\n" + SET_TEXT_COLOR_WHITE;
        if (observing){
            ws.leaveGame(authToken,gameID);
            state = State.LOGGEDIN;
            return leftGameMessage;
        }

        ws.leaveGame(authToken,gameID);
        state = State.LOGGEDIN;
        return leftGameMessage;
    }

    public ChessPosition parsePositionInput(String input) throws ResponseException{
        char tempChar = input.charAt(0);
        String stringNum = input.substring(1);
        int row = Integer.parseInt(stringNum);
        String letter = String.valueOf(tempChar);

        int col = 1;
        col = whiteKey(letter);
        return new ChessPosition(row,col);
    }

    public int whiteKey(String letter){
        int i = 1;
        for (String thing : letters){
            if (thing.equals(letter)){
                return i;
            }
            i++;
        }
        return 0;
    }

    public void printPrompt(){
        if (observing){
            System.out.print("\n" + SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_MAGENTA + "[" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + "OBSERVING" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + "]" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC);
        } else {
            System.out.print("\n" + SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "[" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + "IN_GAME" + EscapeSequences.SET_TEXT_COLOR_BLUE + "]" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC);
        }
    }

    public String help() {
        if (side==3){
            side=1;
            observing=true;
        }

        return SET_TEXT_COLOR_WHITE + """
                \n
                \tredraw - redraw the chess board
                \tleave - leaves chess game
                \tmove <POS> <POS> - makes a move in the chess game
                \tresign - allows the user to resign chess game
                \tlegalMoves <PIECE> - highlights legal moves, ex: legalmoves c2\n""" +
                "\thelp - with possible commands\n";
    }

    private void handleServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> handleNotification(message);
            case LOAD_GAME    -> handleLoadGame(message);
            case ERROR        -> handleError(message);
        }
    }

    private void handleNotification(ServerMessage message) {
        System.out.println("\n" + message.getMessage());
        printPrompt();
    }

    private void handleLoadGame(ServerMessage message) {
        ChessGame game = message.getGame();
        DrawChessHelper draw = new DrawChessHelper(game);

        if (side == 2) {
            draw.drawChess(game, null, null, ChessGame.TeamColor.BLACK);
        } else {
            draw.drawChess(game, null, null, ChessGame.TeamColor.WHITE);
        }

        tempGame = game;
        printPrompt();
    }

    private void handleError(ServerMessage message) {
        System.out.print(message.getErrorMessage() + "\n");
        printPrompt();
    }

}