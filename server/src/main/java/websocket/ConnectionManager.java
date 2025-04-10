package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session, int gameID) {
        var connection = new Connection(visitorName, session, gameID);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, ServerMessage message, boolean includeSender, int gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        String json = new Gson().toJson(message);

        for (var c : connections.values()) {
            if (c.gameID == gameID && c.session.isOpen()) {
                if (!includeSender && Objects.equals(c.username, excludeVisitorName)) {continue;}
                System.out.println("[broadcast] - [" + c.username + "]\t" + json);
                c.send(json);
            } else if (!c.session.isOpen()) {
                removeList.add(c);
            }
        }


        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }


//    public void broadcastToGame(int gameID, ServerMessage.ServerMessageType notification) throws IOException {
//        // Example: iterate through the connections
//        for (var connection : connections.values()) {
//            if(connection.gameID == gameID && connection.session.isOpen()){
//                connection.send(notification.toString());
//            }
//        }
//    }
}