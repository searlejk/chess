package ui;

import chess.*;
import com.google.gson.Gson;
import ui.exceptions.ResponseException;
import ui.model.game.*;
import ui.model.other.GetGameRequest;

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

    public GameClient(String serverUrl, String authToken, int side, int gameID) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.side = side; // 1 is white 2 is black, 3 is observing
        this.gameOver = false;
        this.gameID = gameID;
        this.observing = false;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.print(SET_TEXT_COLOR_WHITE);
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
        if (params.length == 1) {
            String input = params[0];
            GameData gameData = getGameData();
            ChessPosition pos = parsePositionInput(input);
            DrawChessHelper draw = new DrawChessHelper(getGame());
            draw.legalMoves(pos, side);
            return "";
        }
        else{
            throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <COORDINATE>" + SET_TEXT_COLOR_WHITE);
        }
    }

    public String redraw(String... params) throws ResponseException{
        // get game from server
        // draw game with draw chess helper
        System.out.print(ERASE_SCREEN);

        try{
            ChessGame game = getGame();
            DrawChessHelper draw = new DrawChessHelper(game);
            if (side==1) {
                draw.drawChessWhite(game, null, null);
            }
            if (side==2) {
                draw.drawChessBlack(game,null,null);
            }
        } catch(Exception e){
            throw new ResponseException(400, "Redraw failed");
        }
        return "";
    }

    public String move(String... params) throws ResponseException{
        if (observing) {
            return "You are observing, no moving permitted";
        }else if (gameOver) {
            return "The game is over, no moving permitted";
        }

        if (params.length == 2) {
            System.out.print(ERASE_SCREEN);
            System.out.flush();

            var serializer = new Gson();
            GameData gameData = getGameData();

            ChessGame game = getGame();
            ChessPosition startPos = parsePositionInput(params[0]);
            ChessPosition endPos = parsePositionInput(params[1]);
            ChessMove move = new ChessMove(startPos, endPos, null);

            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                return "Invalid Move, try again";
            } catch (NullPointerException e) {
                return "Chess Game is missing";
            }

            String stringGame = serializer.toJson(game);

            GameData newGameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    stringGame);

            DrawChessHelper draw = new DrawChessHelper(game);
            if (side == 1) {
                draw.drawChessWhite(game, null, null);
            }
            if (side == 2) {
                draw.drawChessBlack(game, null, null);
            }

            try {
                server.updateGame(newGameData);
            } catch (Exception e) {
                return "Failed to upload game to server";
            }
            return "";
        } else{
            throw new ResponseException(400, SET_TEXT_COLOR_YELLOW + "Expected: <COORDINATE> <COORDINATE> " + SET_TEXT_COLOR_WHITE);
        }
    }

    public String resign(String... params){
        // get game from server
        // draw game with draw chess helper

        if (observing) {
            return "You are observing, try leave instead";
        } else if (gameOver) {
            return"The game has already been finished\n";
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you would like to resign? Y/N\n");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("y") | answer.equals("yes")){
            gameOver = true;

            System.out.print("You Have Resigned");
        } else{
            System.out.print("Resignation cancelled");
        }
        return "";
    }

    public String leave(String... params) throws ResponseException {
        String leftGameMessage = SET_TEXT_COLOR_BLUE + "you left the game\n" + SET_TEXT_COLOR_WHITE;
        if (observing){
            state = State.LOGGEDIN;
            return leftGameMessage;
        }

        GameData old = server.getGame(new GetGameRequest(String.valueOf(gameID),authToken));
        GameData newGameData = null;
        if (side==1){
            newGameData = new GameData(gameID,null,old.blackUsername(),old.gameName(),old.game());
        }
        if (side==2){
            newGameData = new GameData(gameID,old.whiteUsername(),null,old.gameName(),old.game());
        }

        server.updateGame(newGameData);
        state = State.LOGGEDIN;
        return leftGameMessage;
    }

    public ChessPosition parsePositionInput(String input) throws ResponseException{
        char tempChar = input.charAt(0);
        String stringNum = input.substring(1);
        int row = Integer.parseInt(stringNum);
        String letter = String.valueOf(tempChar);
        List<String> letters = Arrays.asList("a","b","c","d","e","f","g");
        List<Integer> nums = Arrays.asList(1,2,3,4,5,6,7,8);
        if (!letters.contains(letter)){
            throw new ResponseException(400, "letter for coordinate was invalid: " + letter);
        }
        if (!nums.contains(row)){
            throw new ResponseException(400, "number for coordinate was invalid: " + row);
        }
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

    public GameData getGameData(){
        GetGameRequest getGameRequest = new GetGameRequest(gameID.toString(),authToken);
        try{
            return server.getGame(getGameRequest);
        } catch(Exception e){
            return null;
        }
    }

    public ChessGame getGame(){
        var serializer = new Gson();
        GameData gameData = getGameData();
        String stringGame = gameData.game();
        return serializer.fromJson(stringGame,ChessGame.class);
    }

    public String help() {
        if (side==3){
            side=1;
            observing=true;
        }
        return  SET_TEXT_COLOR_WHITE + """
                \n
                \tredraw - redraw the chess board
                \tleave - leaves chess game
                \tmove <POS> <POS> - makes a move in the chess game
                \tresign - allows the user to resign chess game
                \tlegalMoves <PIECE> - highlights legal moves, ex: legalmoves c2""" +
                "\thelp - with possible commands\n";
    }
}