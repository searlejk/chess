package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataAccessProvider;
import model.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private static final DataAccess dataAccess = DataAccessProvider.dataAccess;

    public static AuthData makeAuthData(String username) {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken,username);
    }

    public static RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        ///  If username is already used, give error
        if (dataAccess.getUser(username)!=null){
            throw new DataAccessException("Username is taken");
        }

        ///  Otherwise
        ///  1) add new user
        UserData user = new UserData(username,password,email);
        dataAccess.addUser(user);

        ///  2) Create authToken and store in authData
        AuthData authData = makeAuthData(username);
        dataAccess.addAuthData(authData);

        return new RegisterResult(username,authData.authToken());
    }

    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        ///  if username is in database
        if (dataAccess.getUser(username)!=null){
            /// If password matches
            if (Objects.equals(dataAccess.getUser(username).password(), password)){
                // Do nothing if everything is correct, thus hitting the block below
            } else{
                throw new DataAccessException("Incorrect password");
            }

        } else{
            throw new DataAccessException("Username not in database");
        }

        ///  1) Create authToken and store in authData
        AuthData authData = makeAuthData(username);
        dataAccess.addAuthData(authData);

        return new LoginResult(username,authData.authToken());
    }

    public static LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        UserData user = dataAccess.getUserByAuth(authToken);

        ///  if there is a user with that authToken
        if (user!=null){
            ///  Clear their authToken data
            dataAccess.deleteAuth(authToken);
        } else{
            ///  if there is NO user with that authToken
            throw new DataAccessException("No User with authToken: "+authToken);
        }

        return new LogoutResult();
    }

}