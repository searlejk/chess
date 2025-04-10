package websocket;

import chess.ChessGame;
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
//            case LEAVE -> leaveGame(command.getUsername());
            }
        }catch(Exception e){
            System.out.print("onMessage Failed");
        }
    }

    private void joinGame(UserGameCommand command, Session session, String message) throws IOException {
        DataAccess data = DataAccessProvider.getDataAccess();
        command.getGameID();
        ServerMessage tempError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);

        String username = getUsername(data, command.getAuthToken());
//        if (gameData==null){
//            ServerMessage temp = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
//            ServerMessage badGameID = temp.errorMessage("ERROR: Incorrect gameID");
//            var serializer = new Gson();
//            String badGameIDMessage = serializer.toJson(badGameID);
//            session.getRemote().sendString(badGameIDMessage);
//        }

        GameData gameData = getGameData(data,command.getGameID(),username);
        if (gameData==null){
            ServerMessage badGameID = tempError.errorMessage("ERROR: Incorrect gameID");
            var serializer = new Gson();
            String badGameIDMessage = serializer.toJson(badGameID);
            session.getRemote().sendString(badGameIDMessage);
            return;
        }
        int gameID = gameData.gameID();
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        String teamColor = getTeamColor(gameData,username);

        ServerMessage notifyMSG = notificationMessage.notification(username + " has joined playing as " + teamColor);
        ChessGame game = getGame(gameData);
        ServerMessage loadMSG = loadGameMessage.loadGame(game);




        connections.add(username, session, gameID);
        connections.broadcast(username, loadMSG);
        connections.broadcast(username, notifyMSG);
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
        return "UNKNOWN";
    }
}