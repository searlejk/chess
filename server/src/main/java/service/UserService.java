package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.RegisterResponse;
import model.RegisterRequest;
import model.UserData;

import java.util.UUID;

public class UserService {
    private static final MemoryDataAccess dataAccess = new MemoryDataAccess();

    /*
        I want to do this
        1) Get user to see if it exists
        2) if not then CreateUser
        3) CreateAuth
        4) return register result
         */
    public static AuthData makeAuthData(String username) {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken,username);
    }

    public static RegisterResponse register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        ///  If username is already used, give error
        if (dataAccess.getUser(username)!=null){
            return new RegisterResponse(null, "Error: Username already taken");
        }

        ///  Otherwise
        ///  1) add new user
        UserData user = new UserData(username,password,email);
        dataAccess.addUserData(user);

        ///  2) Create authToken
        AuthData authData = makeAuthData(username);
        dataAccess.addAuthData(authData);

        return new RegisterResponse(username,authData.authToken());
    }




    /// public LoginResult login(LoginRequest loginRequest) {}
    /// public void logout(LogoutRequest logoutRequest) {}
}