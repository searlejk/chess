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
    Boolean isLoggedIn(String username) throws DataAccessException;
    Collection<GameData> listGames();

    GameData addGame(Integer inputGameID,GameData gameData) throws exception.ResponseException;

    GameData getGame(int gameID) throws exception.ResponseException;

    void remGame(int gameID);

    void clearUsersAndAuth() throws exception.ResponseException;

    void clearGames() throws DataAccessException;

    Collection<UserData> listUserDatas() throws exception.ResponseException;

    Collection<AuthData> listAuthDatas();

    boolean checkPassword(String username, String password) throws exception.ResponseException;
}