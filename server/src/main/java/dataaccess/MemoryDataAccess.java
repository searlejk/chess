package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private int nextId = 1;
    final private HashMap<String, UserData> Users = new HashMap<>();
    final private HashMap<Integer, GameData> Games = new HashMap<>();
    final private HashMap<String, AuthData> authDatas = new HashMap<String, AuthData>();

    public UserData addUserData(UserData user) {
        user = new UserData(user.username(), user.password(),user.email());

        Users.put(user.username(), user);
        return user;
    }

    public Collection<UserData> listUserDatas() {
        return Users.values();
    }


    public UserData getUserData(String username) {
        for (UserData user : Users.values()){
            if (user.username()==username){
                System.out.println("username: "+username+" found");
                return user;
            }
            else{
                System.out.println("username: "+username+" not found");
                return user;
            }

        }
        return null;
    }

    public UserData addUser(UserData user) {
        user = new UserData(user.username(), user.password(),user.email());

        Users.put(user.username(), user);
        return user;
    }

    public void addAuthData(AuthData authData) {
        authDatas.put(authData.authToken(),authData);
    }

    public UserData getUser(String username) {
        return Users.get(username);
    }

}