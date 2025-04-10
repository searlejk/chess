package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        System.out.println("Received raw message: " + message.getServerMessageType());
        String json = new Gson().toJson(message);
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
//                if (!c.username.equals(excludeVisitorName)) {
                    System.out.println("[Connection Manager] - Sending Message in JSON:\n"+json+"\n");
                    c.send(json);
//                }
            } else {
                removeList.add(c);
            }
        }


        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }


    public void broadcastToGame(int gameID, ServerMessage.ServerMessageType notification) throws IOException {
        // Example: iterate through the connections
        for (var connection : connections.values()) {
            if(connection.gameID == gameID && connection.session.isOpen()){
                connection.send(notification.toString());
            }
        }
    }
}