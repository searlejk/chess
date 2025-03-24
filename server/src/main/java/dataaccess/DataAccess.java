package dataaccess;

import exceptions.DataAccessException;
import model.user.UserData;
import model.user.AuthData;
import model.game.GameData;

import java.util.Collection;

public interface DataAccess {
    UserData addUser(UserData userdata) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void addAuthData(AuthData authData) throws DataAccessException;
    UserData getUserByAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    Collection<GameData> listGames() throws exception.ServerResponseException;

    GameData addGame(Integer inputGameID,GameData gameData) throws exception.ServerResponseException;

    GameData getGame(int gameID) throws exception.ServerResponseException;

    void remGame(int gameID) throws DataAccessException;

    void clearUsersAndAuth() throws exception.ServerResponseException;

    void clearGames() throws DataAccessException;

    boolean checkPassword(String username, String password) throws exception.ServerResponseException;
}