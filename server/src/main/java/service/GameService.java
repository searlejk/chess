package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import Exceptions.DataAccessException;
import dataaccess.DataAccessProvider;
import model.*;

import static org.eclipse.jetty.util.LazyList.size;

public class GameService {
    private static final DataAccess dataAccess = DataAccessProvider.dataAccess;

    public static void clearGames(){
        dataAccess.clearGames();
    }

    public static ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException{
        String authToken = listGamesRequest.authToken();

        ///  if there is a user with that authToken
        if (dataAccess.getUserByAuth(authToken)!=null){
            ///  Do nothing

        } else{
            ///  if there is NO user with that authToken
            throw new DataAccessException("No User with authToken: "+authToken);
        }

        return new ListGamesResult(dataAccess.listGames());
    }
    public static CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String gameName = createGameRequest.gameName();
        String authToken = createGameRequest.authToken();


        if (dataAccess.getUserByAuth(authToken)!=null){
            ///  Do nothing

        } else{
            ///  if there is NO user with that authToken
            throw new DataAccessException("No User with authToken: " + authToken);
        }

        int gameID = size(dataAccess.listGames());
        GameData gameData = new GameData(gameID,"","",gameName,new ChessGame());
        dataAccess.addGame(gameData,gameID);

        return new CreateGameResult(gameID);
    }

    public static EmptyResult joinGame(JoinRequest joinRequest) throws DataAccessException {
        int gameID = joinRequest.gameID();
        String authToken = joinRequest.authToken();
        ChessGame.TeamColor teamColor = joinRequest.playerColor();

        UserService.checkAuthToken(authToken);

        ///  if the game doesn't exist
        if (dataAccess.getGame(gameID)==null) {
            throw new
        }
            ///  add username to correct color in gamedata
            /*
            1) get username from authtoken userdata
            2) get gameData
            3) delete old game data
            4) make new gameData with username added to WHITE or BLACK

             */
            UserData user = dataAccess.getUserByAuth(authToken);
            String username = user.username();
            GameData gamedata = dataAccess.getGame(gameID);
            dataAccess.remGame(gameID);
            GameData newGameData = null;
            if (teamColor!=null) {
                if (teamColor == "WHITE") {
                    newGameData = new GameData(gameID, username, gamedata.blackUsername(), gamedata.gameName(), gamedata.game());
                }
                if (teamColor == "BLACK") {
                    newGameData = new GameData(gameID, gamedata.whiteUsername(), username, gamedata.gameName(), gamedata.game());
                } else {
                    throw new DataAccessException("Incorrect Color Type given: " + teamColor);
                }
            } else{
                throw new DataAccessException("null color: " + teamColor);
            }
            dataAccess.addGame(newGameData, gameID);

        } else {
            throw new DataAccessException("No such game with gameID: " + gameID);
        }

        return new EmptyResult();

    }

}
