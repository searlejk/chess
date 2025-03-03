package dataaccess;

import model.UserData;
///import model.AuthData;
import model.GameData;

import java.util.Collection;

public interface DataAccess {

    UserData addUser(UserData userdata) throws DataAccessException;

    ///Collection<GameData> listGames(); /// Add throws Exception here

    UserData getUser(String username) throws DataAccessException;

    ///void deleteUser(String username); /// Add throws Exception here

    ///void clear(); /// Add throws Exception here
}