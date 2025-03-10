package dataaccess;

import exceptions.DataAccessException;
import exceptions.InvalidAuthToken;
import model.user.UserData;
import model.user.AuthData;
import model.game.GameData;

import java.util.Collection;

public interface DataAccessMySqlInterface {
    UserData addUser(UserData userdata) throws exception.ResponseException;
    UserData getUser(String username) throws exception.ResponseException;
    void addAuthData(AuthData authData) throws exception.ResponseException;
    UserData getUserByAuth(String authToken) throws exception.ResponseException;
    void deleteAuth(String authToken) throws exception.ResponseException;

    Collection<GameData> listGames();

    GameData addGame(Integer inputGameID,GameData gameData);

    GameData getGame(int gameID);

    void remGame(int gameID);

    void clearUsersAndAuth();

    void clearGames();

    Collection<UserData> listUserDatas() throws exception.ResponseException;

    Collection<AuthData> listAuthDatas();
}