package service;

import exceptions.IncorrectCredentialsException;
import exceptions.InvalidAuthToken;
import dataaccess.DataAccess;
import exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.other.EmptyResult;
import model.user.*;
import org.mindrot.jbcrypt.BCrypt;

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
            if (!DATA_ACCESS.checkPassword(username,password)){
                throw new IncorrectCredentialsException("Incorrect password");
            }

            ///  if username & password correct:
            AuthData authData = makeAuthData(username);
            DATA_ACCESS.addAuthData(authData);
            return new LoginResult(username,authData.authToken());
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