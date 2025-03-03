package service;

import Exceptions.NoGameFoundException;
import chess.ChessGame;
import dataaccess.DataAccess;
import Exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.*;

import static org.eclipse.jetty.util.LazyList.size;

public class GameService {
    private static final DataAccess dataAccess = DataAccessProvider.dataAccess;

    public static void clearGames() {
        dataAccess.clearGames();
    }

    public static ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String authToken = listGamesRequest.authToken();

        ///  check authToken
        UserService.checkAuthToken(authToken);

        return new ListGamesResult(dataAccess.listGames());
    }

    public static CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String gameName = createGameRequest.gameName();
        String authToken = createGameRequest.authToken();


        if (dataAccess.getUserByAuth(authToken) != null) {
            ///  Do nothing

        } else {
            ///  if there is NO user with that authToken
            throw new DataAccessException("No User with authToken: " + authToken);
        }

        int gameID = size(dataAccess.listGames());
        GameData gameData = new GameData(gameID, "", "", gameName, new ChessGame());
        dataAccess.addGame(gameData, gameID);

        return new CreateGameResult(gameID);
    }

    public static void joinGame(JoinRequest joinRequest) throws DataAccessException {
        int gameID = joinRequest.gameID();
        String authToken = joinRequest.authToken();
        String teamColor = joinRequest.playerColor().toString();

        /// check authToken
        UserService.checkAuthToken(authToken);

        /*
            1) get username from authToken userdata
            2) get gameData
            3) delete old game data
            4) make new gameData with username added to WHITE or BLACK

             */

        ///  if the game doesn't exist
        if (dataAccess.getGame(gameID) == null || dataAccess.listGames()==null) {
            throw new NoGameFoundException("Error: No game for gameID: " + gameID);
        }
        /// get username
        String username = dataAccess.getUserByAuth(authToken).username();

        /// save old game locally
        /// delete old game from db
        GameData oldGame = dataAccess.getGame(gameID);
        dataAccess.remGame(gameID);

        GameData newGame = null;
        /// make new game if white
        if (teamColor.equals("WHITE")) {
            newGame = new GameData(gameID, username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        }

        /// make new game if black
        if (teamColor.equals("BLACK")) {
            newGame = new GameData(gameID, oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
        }

        ///  add updated game to database
        dataAccess.addGame(newGame, gameID);


    }
}