package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> authData = new HashMap<>();

    public Collection<UserData> listUserDatas() {
        return users.values();
    }

    public Collection<AuthData> listAuthDatas() {
        return authData.values();
    }

    public UserData addUser(UserData user) {
        user = new UserData(user.username(), user.password(),user.email());

        users.put(user.username(), user);
        System.out.println(users.values());
        return user;
    }

    public void addAuthData(AuthData authData) {
        this.authData.put(authData.authToken(),authData);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public UserData getUserByAuth(String authToken){
        String username = authData.get(authToken).username();
        if (username!=null) {
            return users.get(username);
        }
        return null;
    }

    public void deleteAuth(String authToken){
        authData.remove(authToken);
    }

    public Collection<GameData> listGames(){
        return games.values();
    }

    public GameData addGame(Integer inputGameID, GameData gameData) {
        int gameID;
        if (inputGameID==0){
            gameID = new Random().nextInt(1000);
        } else {
            gameID = inputGameID;
        }
        GameData newGameData = new GameData(gameID,gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
        System.out.println("Added game " + gameID + ": " + newGameData.gameName());
        games.put(gameID,newGameData);
        System.out.println(games.values());
        return newGameData;
    }

    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    public void remGame(int gameID){
        games.remove(gameID);
    }

    public void clearUsersAndAuth() {
        authData.clear();
        users.clear();
    }

    public void clearGames() {
        games.clear();
    }
}