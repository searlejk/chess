package websocket;

import com.google.gson.Gson;
import model.exceptions.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


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
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        System.out.print("[onMessage] - Message Received: \n" + message + "\n");
        switch (command.getCommandType()) {
            case CONNECT -> joinGame(command.getUsername(), session, command.getTeamColor());
            case LEAVE -> leaveGame(command.getUsername());
        }
    }

    private void joinGame(String username, Session session, String teamColor) throws IOException {
        connections.add(username, session);
        var message = String.format("%s has joined the game playing %s", username,teamColor);
        var sMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        sMessage.message = message;
        connections.broadcast(username, sMessage);
    }

    private void leaveGame(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var sMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        sMessage.message = message;
        connections.broadcast(visitorName, sMessage);
    }

    public void sendMessage(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = message;
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}