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
        command.getGameID();
        ServerMessage tempError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        var ser = new Gson();

        String username = getUsername(data, command.getAuthToken());
        GameData gameData = getGameData(data,command.getGameID(),username);
        if (username==null){
            ServerMessage badAuth = tempError.errorMessage("ERROR: Invalid AuthToken");
            var serializer = new Gson();
            String badAuthMessage = serializer.toJson(badAuth);
            session.getRemote().sendString(badAuthMessage);
            return;
        } /// Bad AuthToken
        if (gameData==null){
            ServerMessage badGameID = tempError.errorMessage("ERROR: Incorrect gameID");
            var serializer = new Gson();
            String badGameIDMessage = serializer.toJson(badGameID);
            session.getRemote().sendString(badGameIDMessage);
            return;
        } /// Bad GameID
        ChessGame game = getGame(gameData);

        int gameID = gameData.gameID();
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        String teamColor = getTeamColor(gameData,username);
        ServerMessage notifyMSG;
        if (!Objects.equals(teamColor, "OBSERVER")) {
            notifyMSG = notificationMessage.notification(username + " has joined playing as " + teamColor);
        } else{
            notifyMSG = notificationMessage.notification(username + " is now observing the game");
        }

        ServerMessage loadMSG = loadGameMessage.loadGame(game);

        session.getRemote().sendString(ser.toJson(loadMSG));
        connections.broadcast(username, notifyMSG);
        connections.add(username, session, gameID);
//        connections.broadcast(username, loadMSG);
    }

    private void makeMove(UserGameCommand command, Session session, String message) throws IOException {
        DataAccess data = DataAccessProvider.getDataAccess();
        String username = getUsername(data, command.getAuthToken());
        ChessMove move = command.getMove();
        GameData gameData = getGameData(data,command.getGameID(),username);
        ChessGame game = getGame(gameData);
        ChessGame.TeamColor turnColor = game.getTeamTurn();
        var userColor = getTeamColor(gameData,username);
        ServerMessage tempError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        var ser = new Gson();
        ChessGame.TeamColor currColor = null;

        if (Objects.equals(userColor, "WHITE")){
            currColor = ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(userColor, "BLACK")){
            currColor = ChessGame.TeamColor.BLACK;
        }
        if (Objects.equals(userColor, "OBSERVER")){
            ServerMessage gameOverTemp = tempError.errorMessage("You are observing the game, no moves allowed");
            session.getRemote().sendString(ser.toJson(gameOverTemp));
            return;
        }

        if (game.isInCheckmate(turnColor) || game.isInStalemate(turnColor)){
            ServerMessage gameOverTemp = tempError.errorMessage("The game is over, no moves allowed");
            session.getRemote().sendString(ser.toJson(gameOverTemp));
            return;
        }

        if (currColor!=game.getBoard().getPiece(move.getStartPosition()).getTeamColor()){
            ServerMessage gameOverTemp = tempError.errorMessage("You must move a "+currColor+" piece");
            session.getRemote().sendString(ser.toJson(gameOverTemp));
            return;
        }

        try {
            game.makeMove(move);
            data.remGame(gameData.gameID());
            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(),gameData.blackUsername(),gameData.gameName(),ser.toJson(game));
            data.addGame(gameData.gameID(), updatedGame);
        } catch(InvalidMoveException e){
            ServerMessage gameOverTemp = tempError.errorMessage("ERROR: Invalid Move");
            session.getRemote().sendString(ser.toJson(gameOverTemp));
            return;
        } catch(Exception e){
            ServerMessage gameOverTemp = tempError.errorMessage("ERROR: Update Game Failed");
            session.getRemote().sendString(ser.toJson(gameOverTemp));
            return;
        }



        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        ServerMessage loadMSG = loadGameMessage.loadGame(game);
        connections.broadcast(username, loadMSG);
        /// send messages to other players:
        ServerMessage notifyOthers = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        ServerMessage notifyMessage = notifyOthers.notification(username + " made a move from " + move);
        connections.broadcast(username,notifyMessage);
    }

    private void resign(UserGameCommand command, Session session, String message) throws IOException {
        DataAccess data = DataAccessProvider.getDataAccess();
        String username = getUsername(data,command.getAuthToken());
        ServerMessage resign = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        ServerMessage resignMSG = resign.notification(username+ " has resigned from the game");
        connections.broadcast(null, resignMSG);
        connections.remove(username);
    }

    private void leaveGame(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var sMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
//        sMessage.message = message;
        connections.broadcast(visitorName, sMessage);
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
}