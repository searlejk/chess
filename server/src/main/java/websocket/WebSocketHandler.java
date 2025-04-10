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
        var json = new Gson();
        String username = getUsername(data, command.getAuthToken());
        GameData gameData = getGameData(data,command.getGameID(),username);
        ServerMessage sMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        ChessGame game = json.fromJson(gameData.game(), ChessGame.class);
        ServerMessage serverMessage = sMessage.loadGame(game);


        connections.add(username, session);
//        String outputMessage = String.format("%s has joined the game playing %s", username, teamColor);
//        sMessage.message = outputMessage;
        connections.broadcast(username, serverMessage);
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
        return "getUsernameFAILED";
    }

    public GameData getGameData(DataAccess data, int gameID, String username) {

        try {
            GameData gameData = data.getGame(gameID);
            return gameData;
        } catch (Exception e) {
            return null;
        }
    }
}