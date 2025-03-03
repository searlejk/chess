package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private int nextId = 1;
    final private HashMap<String, UserData> Users = new HashMap<>();
    final private HashMap<Integer, GameData> Games = new HashMap<>();
    final private HashMap<String, AuthData> authData = new HashMap<String, AuthData>();

    public Collection<UserData> listUserDatas() {
        return Users.values();
    }

    public UserData addUser(UserData user) {
        user = new UserData(user.username(), user.password(),user.email());

        Users.put(user.username(), user);
        return user;
    }

    public void addAuthData(AuthData authData) {
        this.authData.put(authData.authToken(),authData);
    }

    public UserData getUser(String username) {
        return Users.get(username);
    }

    public UserData getUserByAuth(String authToken){
        String username = authData.get(authToken).username();
        return Users.get(username);
    }

    public void deleteAuth(String authToken){
        authData.remove(authToken);
    }

    public Collection<GameData> listGames(){
        return Games.values();
    }

    public void addGame(GameData gameData, int gameID) {
        Games.put(gameID,gameData);
    }
}