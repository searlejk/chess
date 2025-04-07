package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.glassfish.grizzly.http.server.Response;
import ui.exceptions.ResponseException;
import ui.model.game.*;
import ui.model.other.EmptyResult;
import ui.model.user.*;

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

    public GameClient(String serverUrl, String authToken, int side) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.side = side; // 1 is white 2 is black
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            return switch (cmd) {
//                case "logout" -> logout(params);
//                case "create" -> create(params);
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
        if (params.length ==1) {
            String input = params[0];
            System.out.print("input: " + input+"\n");
            ChessGame game = new ChessGame();
            DrawChessHelper draw = new DrawChessHelper(game);
            System.out.print("side: "+ side+"\n");
            draw.legalMoves(input, side);
            return "Board Drawn?";
        }
        else{
            throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <SQUARE>" + SET_TEXT_COLOR_WHITE);
        }
    }

    public String redraw(String... params){
        // get game from server
        // draw game with draw chess helper

        return "";
    }

    public String leave(String... params) throws ResponseException {
        state = State.LOGGEDIN;
        return SET_TEXT_COLOR_BLUE + "you left the game\n" + SET_TEXT_COLOR_WHITE;
    }




    public String help() {
        return  SET_TEXT_COLOR_WHITE + """
                \n
                \tredraw - redraw the chess board
                \tleave - leaves chess game
                \tmove <MOVE> - makes a move in the chess game
                \tresign - allows the user to resign chess game
                \tlegalMoves <PIECE> - highlights legal moves, ex: legalmoves c2""" + "\n\t" +
                "\thelp - with possible commands\n";
    }
}