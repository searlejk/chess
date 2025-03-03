package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataAccessProvider;
import model.*;

import static org.eclipse.jetty.util.LazyList.size;

public class GameService {
    private static final DataAccess dataAccess = DataAccessProvider.dataAccess;

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
            throw new DataAccessException("No User with authToken: "+authToken);
        }

        int gameID = size(dataAccess.listGames());
        GameData gameData = new GameData(gameID,"","",gameName,new ChessGame());
        dataAccess.addGame(gameData,gameID);

        return new CreateGameResult(gameID);
    }
}
