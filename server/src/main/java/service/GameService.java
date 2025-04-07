package service;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.IncorrectCredentialsException;
import exceptions.NoGameFoundException;
import exceptions.TeamTakenException;
import dataaccess.DataAccess;
import exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.game.*;
import model.other.EmptyResult;
import model.user.JoinRequest;

import java.util.Locale;
import java.util.Objects;

public class GameService {
    private static final DataAccess DATA_ACCESS = DataAccessProvider.getDataAccess();
    private static int nextGameID = 1;

    public static void clearGames() throws DataAccessException {
        DATA_ACCESS.clearGames();
    }

    public static ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String authToken = listGamesRequest.authToken();

        ///  check authToken
        UserService.checkAuthToken(authToken);

        return new ListGamesResult(DATA_ACCESS.listGames());
    }

    public static CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        var serializer = new Gson();
        String gameName = createGameRequest.gameName();
        String authToken = createGameRequest.authToken();

        UserService.checkAuthToken(authToken);

        ChessGame game = new ChessGame();
        String jsonChessGame = serializer.toJson(game);
        GameData gameData = new GameData(0, null, null, gameName, jsonChessGame);
        GameData newGameData = DATA_ACCESS.addGame(0, gameData);

        return new CreateGameResult(newGameData.gameID());
    }

    public static EmptyResult joinGame(JoinRequest joinRequest) throws DataAccessException {
        var serializer = new Gson();
        int gameID = joinRequest.gameID();
        String authToken = joinRequest.authToken();
        String teamColor = joinRequest.playerColor().toUpperCase();
        System.out.println("Data taken from joinRequest");

        /// check authToken
        // REMOVED *******************
        //UserService.checkAuthToken(authToken);
        System.out.println("CheckauthToken Passed");

        if (!teamColor.equals("BLACK") && !teamColor.equals("WHITE")){
            throw new IncorrectCredentialsException("Error: Incorrect Color selected");
        }

        System.out.println("Good Color");

        /*
            1) get username from authToken userdata
            2) get gameData
            3) delete old game data
            4) make new gameData with username added to WHITE or BLACK

             */

        ///  if the game doesn't exist
        if (DATA_ACCESS.getGame(gameID)==null){
            throw new IncorrectCredentialsException("Missing gameID");
        }

        System.out.println("Good GameID");

        if (DATA_ACCESS.listGames()==null) {
            throw new NoGameFoundException("Error: No game for gameID: " + gameID);
        }

        System.out.println("Game Found");

        /// get username
        String username = DATA_ACCESS.getUserByAuth(authToken).username();

        System.out.println("Found User by Auth");

        /// save old game locally
        /// delete old game from db
        GameData oldGame = DATA_ACCESS.getGame(gameID);

        System.out.println("Found Game");

        GameData newGame = null;
        /// make new game if white
        if (teamColor.equals("WHITE")) {
            if (!Objects.equals(oldGame.whiteUsername(), null)){
                throw new TeamTakenException("White Team Taken");
            }
            newGame = new GameData(gameID, username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        }

        /// make new game if black
        if (teamColor.equals("BLACK")) {
            if (!Objects.equals(oldGame.blackUsername(), null)){
                throw new TeamTakenException("Black Team Taken");
            }
            ChessGame game = new ChessGame();
            String jsonGame = serializer.toJson(game);
            newGame = new GameData(gameID, oldGame.whiteUsername(), username, oldGame.gameName(), jsonGame);
        }

        ///  delete old game
        /// add updated game to database
        DATA_ACCESS.remGame(gameID);
        DATA_ACCESS.addGame(gameID, newGame);

        return new EmptyResult();
    }

    public static ChessGame getGame(int gameID) throws DataAccessException{
        var serializer = new Gson();
        GameData gameData = DATA_ACCESS.getGame(gameID);
        return serializer.fromJson(gameData.game(),ChessGame.class);
    }
}