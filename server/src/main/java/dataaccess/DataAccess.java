package dataaccess;

import Exceptions.DataAccessException;
import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public interface DataAccess {
    UserData addUser(UserData userdata) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void addAuthData(AuthData authData) throws DataAccessException;
    UserData getUserByAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

    Collection<GameData> listGames();

    void addGame(GameData gameData,int gameID);

    GameData getGame(int gameID);

    void remGame(int gameID);

    void clearUsersAndAuth();

    void clearGames();

    Collection<UserData> listUserDatas();

    Collection<AuthData> listAuthDatas();
}