package ui;

import chess.*;
import com.google.gson.Gson;
import org.glassfish.grizzly.http.server.Response;
import ui.exceptions.ResponseException;
import ui.model.game.*;
import ui.model.other.EmptyResult;
import ui.model.other.GetGameRequest;
import ui.model.other.GetGameResult;
import ui.model.other.UpdateGameRequest;
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
    private Boolean resigned = false;
    private Integer gameID;

    public GameClient(String serverUrl, String authToken, int side, int gameID) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.side = side; // 1 is white 2 is black
        this.resigned = false;
        this.gameID = gameID;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            return switch (cmd) {
//                case "logout" -> logout(params);
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
        if (params.length ==1) {
            String input = params[0];
            ChessPosition pos = parseMoveInput(input);
            ChessGame game = new ChessGame();
            DrawChessHelper draw = new DrawChessHelper(game);
            draw.legalMoves(pos, side);
            return "";
        }
        else{
            throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <SQUARE>" + SET_TEXT_COLOR_WHITE);
        }
    }

    public String redraw(String... params){
        // get game from server
        // draw game with draw chess helper
        System.out.print(ERASE_SCREEN);
        GetGameRequest getGameRequest = new GetGameRequest(gameID.toString(),authToken);
        try{
            ChessGame game = server.getGame(getGameRequest);
            DrawChessHelper draw = new DrawChessHelper(game);
            if (side==1) {
                draw.drawChessWhite(game, null, null);
            }
            if (side==2) {
                draw.drawChessBlack(game,null,null);
            }
        } catch(Exception e){
            return "Redraw Failed\n";
        }
        return "";
    }

    public String move(String... params){
        // get game from server
        // update game with move
        // print board
        // update serve with new game
        System.out.print(ERASE_SCREEN);
        System.out.flush();

        ChessGame game;
        GetGameRequest getGameRequest = new GetGameRequest(gameID.toString(),authToken);
        System.out.print(ERASE_SCREEN);
        try {
            JoinRequest gameRequest = new JoinRequest("WHITE",gameID, authToken);
            game = server.getGame(getGameRequest);
        } catch(Exception e){
            return "Game could not be found";
        }

        var serializer = new Gson();
        ChessPosition startPos = parseMoveInput(params[0]);
        ChessPosition endPos = parseMoveInput(params[1]);
        ChessMove move = new ChessMove(startPos,endPos,null);
        try {
            game.makeMove(move);
        } catch(InvalidMoveException e){
            return "Invalid Move, try again";
        } catch(NullPointerException e){
            return "Chess Game is missing";
        }

        DrawChessHelper draw = new DrawChessHelper(game);
        if (side==1) {
            draw.drawChessWhite(game, null, null);
        }
        if (side==2) {
            draw.drawChessBlack(game,null,null);
        }

        try{
            String jsonGame = serializer.toJson(game);
            UpdateGameRequest updateGameRequest = new UpdateGameRequest(gameID.toString(),jsonGame);
            server.updateGame(updateGameRequest);
        } catch(Exception e){
            return "Failed to upload game to server";
        }
        return "";
    }

    public String resign(String... params){
        // get game from server
        // draw game with draw chess helper
        if (resigned){
            return"You have already resigned\n";
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you would like to resign? Y/N\n");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("y") | answer.equals("yes")){
            resigned = true;
            System.out.print("You Have Resigned");
        } else{
            System.out.print("Resignation cancelled");
        }
        return "";
    }

    public String leave(String... params) throws ResponseException {
        state = State.LOGGEDIN;
        return SET_TEXT_COLOR_BLUE + "you left the game\n" + SET_TEXT_COLOR_WHITE;
    }

    public ChessPosition parseMoveInput(String input){
        char tempChar = input.charAt(0);
        String stringNum = input.substring(1);
        int row = Integer.parseInt(stringNum);
        String letter = String.valueOf(tempChar);
        int col = 1;
        if (side==1){
            //White
            col = whiteAlphabet(letter);
        }
        if (side==2){
            //Black
            col = blackAlphabet(letter);
        }
        return new ChessPosition(row,col);
    }

    public int whiteAlphabet(String letter){
        if (Objects.equals(letter, "a")){
            return 1;
        }
        if (Objects.equals(letter, "b")){
            return 2;
        }
        if (Objects.equals(letter, "c")){
            return 3;
        }
        if (Objects.equals(letter, "d")){
            return 4;
        }
        if (Objects.equals(letter, "e")){
            return 5;
        }
        if (Objects.equals(letter, "f")){
            return 6;
        }
        if (Objects.equals(letter, "g")){
            return 7;
        }
        if (Objects.equals(letter, "h")){
            return 8;
        }
        return 0;
    }

    public int blackAlphabet(String letter){
        if (Objects.equals(letter, "h")){
            return 1;
        }
        if (Objects.equals(letter, "g")){
            return 2;
        }
        if (Objects.equals(letter, "f")){
            return 3;
        }
        if (Objects.equals(letter, "e")){
            return 4;
        }
        if (Objects.equals(letter, "d")){
            return 5;
        }
        if (Objects.equals(letter, "c")){
            return 6;
        }
        if (Objects.equals(letter, "b")){
            return 7;
        }
        if (Objects.equals(letter, "a")){
            return 8;
        }
        return 0;
    }

    public String help() {
        return  SET_TEXT_COLOR_WHITE + """
                \n
                \tredraw - redraw the chess board
                \tleave - leaves chess game
                \tmove <MOVE> - makes a move in the chess game
                \tresign - allows the user to resign chess game
                \tlegalMoves <PIECE> - highlights legal moves, ex: legalmoves c2""" +
                "\thelp - with possible commands\n";
    }
}