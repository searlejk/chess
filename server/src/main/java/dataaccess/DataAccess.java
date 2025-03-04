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

    Collection<GameData> listGames();

    GameData addGame(Integer inputGameID,GameData gameData);

    GameData getGame(int gameID);

    void remGame(int gameID);

    void clearUsersAndAuth();

    void clearGames();

    Collection<UserData> listUserDatas();

    Collection<AuthData> listAuthDatas();
}