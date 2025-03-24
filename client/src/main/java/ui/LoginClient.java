package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.exceptions.ResponseException;
import ui.model.game.*;
import ui.model.other.EmptyResult;
import ui.model.user.*;

import java.util.*;

import static ui.EscapeSequences.*;

public class LoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    public State state = State.LOGGEDIN;
    private final String authToken;
    private List<Integer> orderedGameID;

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
                case "join" -> join(params);
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
        throw new ResponseException(400, "only type 'list' for list");
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2) {
            int index = Integer.parseInt(params[0]) - 1;
            String playerColor = params[1];
            int gameID = this.orderedGameID.get(index);

            JoinRequest joinRequest = new JoinRequest(playerColor, gameID, authToken);
            try {
                EmptyResult emptyResult = server.join(joinRequest);
            } catch(Exception e){
                return "Join Game Failed";
            }
            ChessGame game = new ChessGame();
            if (params[1].equalsIgnoreCase("WHITE")){
                drawChessWhite(game);
            }
            if (params[1].equalsIgnoreCase("BLACK")){
                drawChessBlack(game);
            }

            return "";
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public void drawChessWhite(ChessGame game){
        System.out.print(ERASE_SCREEN);
        ChessBoard board = game.getBoard();
        String unicodePiece;

        setTopKey("    a  b  c  d  e  f  g  h    ");
        int key = 8;

        for (int row = 8; row >= 1; row--){
            ///  left number key
            setKeyColors(" "+key+" ");
            for (int col = 1; col <= 8; col++){
                ChessPosition pos = new ChessPosition(row,col);

                boardBackgroundColor(row,col,board,pos);

                if (board.getPiece(pos)!=null){
                    ChessPiece piece = board.getPiece(pos);
                    ChessGame.TeamColor color = piece.getTeamColor();
                    unicodePiece = getUnicodePiece(piece);

                    if(color == ChessGame.TeamColor.WHITE){
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    } else{
                        System.out.print(SET_TEXT_COLOR_DARK_GREY);
                    }
                    System.out.print(unicodePiece);
                }
            }
            setKeyColors(" "+key+" ");
            key--;
            newLine();
        }
        setKeyColors("    a  b  c  d  e  f  g  h    ");
    }

    public void drawChessBlack(ChessGame game){
        System.out.print(ERASE_SCREEN);
        ChessBoard board = game.getBoard();
        String unicodePiece;

        setTopKey("    h  g  f  e  d  c  b  a    ");
        int key = 1;

        for (int row = 1; row <= 8; row++){
            ///  left number key
            setKeyColors(" "+key+" ");
            for (int col = 1; col <= 8; col++){
                ChessPosition pos = new ChessPosition(row,col);

                boardBackgroundColor(row,col,board,pos);

                if (board.getPiece(pos)!=null) {
                    ChessPiece piece = board.getPiece(pos);
                    ChessGame.TeamColor color = piece.getTeamColor();
                    unicodePiece = getUnicodePiece(piece);

                    if(color == ChessGame.TeamColor.WHITE){
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    } else{
                        System.out.print(SET_TEXT_COLOR_DARK_GREY);
                    }
                    System.out.print(unicodePiece);
                }
            }
            setKeyColors(" "+key+" ");
            key++;
            newLine();
        }
        setKeyColors("    h  g  f  e  d  c  b  a    ");
    }

    private void resetTextAndBackground(){
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private void setKeyColors(String output){
        System.out.print(EscapeSequences.SET_TEXT_BOLD);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        System.out.print(output);
        resetTextAndBackground();
    }

    private void setTopKey(String output){
        setKeyColors(output);
        newLine();
    }

    private void newLine(){
        System.out.print("\n");
    }

    private void boardBackgroundColor(int row, int col,ChessBoard board, ChessPosition pos){
        if ((row + col) % 2 == 0) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        } else{
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        }

        if (board.getPiece(pos)==null){
            System.out.print("   ");
        }
    }

    private static String getUnicodePiece(ChessPiece piece){
        String teamColor = piece.getTeamColor().toString();
        switch (piece.getPieceType()){
            case PAWN:
                return teamColor.equals("WHITE") ? WHITE_PAWN : BLACK_PAWN;
            case KING:
                return teamColor.equals("WHITE") ? WHITE_KING : BLACK_KING;
            case KNIGHT:
                return teamColor.equals("WHITE") ? WHITE_KNIGHT : BLACK_KNIGHT;
            case QUEEN:
                return teamColor.equals("WHITE") ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK:
                return teamColor.equals("WHITE") ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP:
                return teamColor.equals("WHITE") ? WHITE_BISHOP : BLACK_BISHOP;
            default:
                return EMPTY;
        }
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
}