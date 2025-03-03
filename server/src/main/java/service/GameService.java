package service;

import Exceptions.IncorrectCredentialsException;
import Exceptions.NoGameFoundException;
import Exceptions.TeamTakenException;
import chess.ChessGame;
import dataaccess.DataAccess;
import Exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.*;

import java.util.Objects;

import static org.eclipse.jetty.util.LazyList.size;

public class GameService {
    private static final DataAccess dataAccess = DataAccessProvider.dataAccess;
    private static int nextGameID = 1;

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

        UserService.checkAuthToken(authToken);

        GameData gameData = new GameData(0, null, null, gameName);
        GameData newGameData = dataAccess.addGame(0, gameData);

        return new CreateGameResult(newGameData.gameID());
    }

    public static void joinGame(JoinRequest joinRequest) throws DataAccessException {
        int gameID = joinRequest.gameID();
        String authToken = joinRequest.authToken();
        String teamColor = joinRequest.playerColor();

        /// check authToken
        UserService.checkAuthToken(authToken);

        /*
            1) get username from authToken userdata
            2) get gameData
            3) delete old game data
            4) make new gameData with username added to WHITE or BLACK

             */

        ///  if the game doesn't exist
        if (dataAccess.getGame(gameID)==null){
            throw new IncorrectCredentialsException("Missing gameID");
        }
        if (dataAccess.listGames()==null) {
            throw new NoGameFoundException("Error: No game for gameID: " + gameID);
        }
        /// get username
        String username = dataAccess.getUserByAuth(authToken).username();

        /// save old game locally
        /// delete old game from db
        GameData oldGame = dataAccess.getGame(gameID);

        GameData newGame = null;
        /// make new game if white
        if (teamColor.equals("WHITE")) {
            if (!Objects.equals(oldGame.whiteUsername(), null)){
                throw new TeamTakenException("White Team Taken");
            }
            newGame = new GameData(gameID, username, oldGame.blackUsername(), oldGame.gameName());
        }

        /// make new game if black
        if (teamColor.equals("BLACK")) {
            if (!Objects.equals(oldGame.blackUsername(), null)){
                throw new TeamTakenException("Black Team Taken");
            }
            newGame = new GameData(gameID, oldGame.whiteUsername(), username, oldGame.gameName());
        }

        ///  delete old game
        /// add updated game to database
        dataAccess.remGame(gameID);
        dataAccess.addGame(gameID, newGame);


    }
}