package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.exceptions.ResponseException;
import model.game.GameData;
import model.user.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import dataaccess.DataAccessProvider;


import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        // Log or initialize the session
        System.out.println("A new client connected: " + session.getRemoteAddress().getAddress());

    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            System.out.print("[onMessage] - Message Received: \n" + message + "\n");


            switch (command.getCommandType()) {
                case CONNECT -> joinGame(command, session, message);
                case MAKE_MOVE -> makeMove(command, session, message);
                case RESIGN -> resign(command,session, message);
                case LEAVE -> leaveGame(command,session,message);
            }
        }catch(NullPointerException e){
            System.out.print("Null Pointer in onMessage");
        }catch(IndexOutOfBoundsException e){
            System.out.print("Index out of bound in onMessage");
        }catch(Exception e){
            System.out.print("Exception in onMessage");
        }
    }

    private void joinGame(UserGameCommand command, Session session, String message) throws IOException {
        DataAccess data = DataAccessProvider.getDataAccess();

        String username = getUsername(data, command.getAuthToken());
        GameData gameData = getGameData(data,command.getGameID(),username);
        if (username==null){
            sendDirectMessage(session, getErrorMessage("ERROR: Invalid AuthToken"));
            return;
        } /// Bad AuthToken
        if (gameData==null){
            sendDirectMessage(session, getErrorMessage("ERROR: Incorrect gameID"));
            return;
        } /// Bad GameID

        ChessGame game = getGame(gameData);

        int gameID = gameData.gameID();
        String teamColor = getTeamColor(gameData,username);
        ServerMessage notifyMSG;
        if (!Objects.equals(teamColor, "OBSERVER")) {
            notifyMSG = getNotificationMessage(username + " has joined playing as " + teamColor);
        } else{
            notifyMSG = getNotificationMessage(username + " is now observing the game");
        }

        sendDirectMessage(session, getLoadGameMessage(game));
        connections.broadcast(username, notifyMSG);
        connections.add(username, session, gameID);
    }

    private void makeMove(UserGameCommand command, Session session, String message) throws IOException {
        DataAccess data = DataAccessProvider.getDataAccess();
        String username = getUsername(data, command.getAuthToken());
        ChessMove move = command.getMove();
        GameData gameData = getGameData(data,command.getGameID(),username);
        ChessGame game = getGame(gameData);
        ChessGame.TeamColor turnColor = game.getTeamTurn();
        var userColor = getTeamColor(gameData,username);
        ChessGame.TeamColor currColor = null;

        if (Objects.equals(userColor, "WHITE")){
            currColor = ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(userColor, "BLACK")){
            currColor = ChessGame.TeamColor.BLACK;
        }

        if (Objects.equals(userColor, "OBSERVER")){
            sendDirectMessage(session, getErrorMessage("You are observing the game, no moves allowed"));
            return;
        }

        if (game.isInCheckmate(turnColor) || game.isInStalemate(turnColor)){
            sendDirectMessage(session, getErrorMessage("The game is over, no moves allowed"));
            return;
        }

        if (currColor!=game.getBoard().getPiece(move.getStartPosition()).getTeamColor()){
            sendDirectMessage(session, getErrorMessage("You must move a "+currColor+" piece"));
            return;
        }

        try {
            var ser = new Gson();
            game.makeMove(move);
            data.remGame(gameData.gameID());
            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(),gameData.blackUsername(),gameData.gameName(),ser.toJson(game));
            data.addGame(gameData.gameID(), updatedGame);
        } catch(InvalidMoveException e){
            sendDirectMessage(session, getErrorMessage("ERROR: Invalid Move"));
            return;
        } catch(Exception e){
            sendDirectMessage(session, getErrorMessage("ERROR: Update Game Failed"));
            return;
        }

        ServerMessage loadMSG = getLoadGameMessage(game);
        connections.broadcast(username, loadMSG);
        /// send messages to other players:
        ServerMessage notifyMessage = getNotificationMessage(username + " made a move from " + move);
        connections.broadcast(username,notifyMessage);
    }

    private void resign(UserGameCommand command, Session session, String message) throws IOException {
        DataAccess data = DataAccessProvider.getDataAccess();
        String username = getUsername(data,command.getAuthToken());
        GameData gameData = getGameData(data,command.getGameID(),username);
        String resignedColor = getTeamColor(gameData,username);
        GameData updatedGameData = null;

        if (Objects.equals(resignedColor, "WHITE")){
            updatedGameData = new GameData(gameData.gameID(), "__LOSER__","__WINNER__",gameData.gameName(),gameData.game());
        }
        if (Objects.equals(resignedColor, "BLACK")){
            updatedGameData = new GameData(gameData.gameID(), "__WINNER__","__LOSER__",gameData.gameName(),gameData.game());
        }
        if (Objects.equals(resignedColor, "OBSERVER")){
            sendDirectMessage(session, getErrorMessage("ERROR: You are observing the game, try leave instead"));
            return;
        }
        try {
            data.remGame(gameData.gameID());
            data.addGame(gameData.gameID(), updatedGameData);
        }catch(Exception e){
            sendDirectMessage(session, getErrorMessage("ERROR: resign did not update game correctly"));
            return;
        }

        connections.broadcast(null, getNotificationMessage(username+ " has resigned from the game"));
        connections.remove(username);
    }

    private void leaveGame(UserGameCommand command, Session session, String message) throws IOException {
        DataAccess data = DataAccessProvider.getDataAccess();
        String username = getUsername(data,command.getAuthToken());
        GameData gameData = getGameData(data,command.getGameID(),username);
        String playerColor = getTeamColor(gameData,username);

        GameData updatedGame = switch (playerColor) {
            case "WHITE" -> new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            case "BLACK" -> new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            default -> gameData;
        };

        try{
            if (!playerColor.equals("OBSERVER")){
                data.remGame(gameData.gameID());
                data.addGame(gameData.gameID(), updatedGame);
            }
        } catch(Exception e){
            sendDirectMessage(session, getErrorMessage("ERROR: Game was not updated correctly"));
            return;
        }

        connections.remove(username);
        connections.broadcast(username,getNotificationMessage(username + " has left the game (was " + playerColor+")"));
    }

    public void sendMessage(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public String getUsername(DataAccess data, String authToken) {
        try {
            UserData user = data.getUserByAuth(authToken);
            return user.username();
        } catch (Exception e) {
            System.out.print("get username failed to get username from AuthToken");
        }
        return null;
    }

    public GameData getGameData(DataAccess data, int gameID, String username) {

        try {
            return data.getGame(gameID);
        } catch (Exception e) {
            return null;
        }
    }

    public ChessGame getGame(GameData gameData){
        var json = new Gson();
        return json.fromJson(gameData.game(), ChessGame.class);

    }

    public String getTeamColor(GameData gameData, String username){
        if (Objects.equals(gameData.whiteUsername(), username)){
            return "WHITE";
        }
        if (Objects.equals(gameData.blackUsername(), username)){
            return "BLACK";
        }
        return "OBSERVER";
    }

    public ServerMessage getErrorMessage(String message){
        return new ServerMessage(ServerMessage.ServerMessageType.ERROR).errorMessage(message);
    }

    public ServerMessage getNotificationMessage(String message){
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION).notification(message);
    }

    public ServerMessage getLoadGameMessage(ChessGame game){
        return new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME).loadGame(game);
    }

    public void sendDirectMessage(Session session, ServerMessage message) throws IOException{
        var ser = new Gson();
        try {
            session.getRemote().sendString(ser.toJson(message));
        } catch(IOException e){
            throw new IOException();
        }
    }

//    public ChessGame updateGame(DataAccess data, GameData gameData){
//        data.remGame(gameData.game());
//        data.addgame
//    }
}