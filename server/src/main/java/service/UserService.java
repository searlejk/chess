package service;

import exceptions.IncorrectCredentialsException;
import exceptions.InvalidAuthToken;
import dataaccess.DataAccess;
import exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.other.EmptyResult;
import model.user.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private static final DataAccess DATA_ACCESS = DataAccessProvider.getDataAccess();

    public static void checkAuthToken(String authToken) throws DataAccessException {
        if (DATA_ACCESS.getUserByAuth(authToken)==null){
            throw new InvalidAuthToken("Error: authToken not valid: " + authToken);
        }
    }

    public static void clearUsersAndAuth(){
        try {
            DATA_ACCESS.clearUsersAndAuth();
        }catch (Exception e){
            System.out.print("ClearUsersAndAuth failed in UserService");
        }
    }
    public static AuthData makeAuthData(String username) {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken,username);
    }

    public static RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        try {
            ///  If username is already used, give error
            if (DATA_ACCESS.getUser(username) != null) {
                throw new DataAccessException("Username is taken");
            }
        } catch(DataAccessException e){
            throw new DataAccessException("Username is taken");
        }

        ///  Otherwise
        ///  1) add new user
        UserData user = new UserData(username,password,email);
        DATA_ACCESS.addUser(user);

        ///  2) Create authToken and store in authData
        AuthData authData = makeAuthData(username);
        DATA_ACCESS.addAuthData(authData);

        return new RegisterResult(username,authData.authToken());
    }

    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        Boolean bool;



        ///  if username is missing
        if (DATA_ACCESS.getUser(username)==null) {
            throw new IncorrectCredentialsException("Username not in database");


        } else {
            ///  if incorrect password
            if (!Objects.equals(DATA_ACCESS.getUser(username).password(), password)) {
                throw new IncorrectCredentialsException("Incorrect password");

            } else {
            ///  if username & password correct:

                ///  Add code here that checks if the user has already logged in
                ///  -check authData by username and if it exists throw an error
                try{
                    bool = DATA_ACCESS.isLoggedIn(username);
                    if (bool){
                        throw new DataAccessException("Error: User is already logged in");
                    }
                } catch(Exception e){
                    throw new DataAccessException("Error: is Logged in Error");
                }


                AuthData authData = makeAuthData(username);
                DATA_ACCESS.addAuthData(authData);
                return new LoginResult(username,authData.authToken());
            }
        }

    }

    public static EmptyResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        UserData user = DATA_ACCESS.getUserByAuth(authToken);

        ///  if there is a user with that authToken
        if (user!=null){
            ///  Clear their authToken data
            DATA_ACCESS.deleteAuth(authToken);
        } else{
            ///  if there is NO user with that authToken
            throw new DataAccessException("Error: No User with authToken: "+authToken);
        }

        return new EmptyResult();
    }

}