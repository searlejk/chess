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
    final private HashMap<String, AuthData> authData = new HashMap<>();

    public Collection<UserData> listUserDatas() {
        return Users.values();
    }

    public Collection<AuthData> listAuthDatas() {
        return authData.values();
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
        if (username!=null) {
            return Users.get(username);
        }
        return null;
    }

    public void deleteAuth(String authToken){
        authData.remove(authToken);
    }

    public Collection<GameData> listGames(){
        return Games.values();
    }

    public void addGame(int gameID, GameData gameData) {
        Games.put(gameID,gameData);
    }

    public GameData getGame(int gameID){
        return Games.get(gameID);
    }

    public void remGame(int gameID){
        Games.remove(gameID);
    }

    public void clearUsersAndAuth() {
        authData.clear();
        Users.clear();
    }

    public void clearGames() {
        Games.clear();
    }
}