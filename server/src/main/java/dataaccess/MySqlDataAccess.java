package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import exceptions.DataAccessException;
import exceptions.InvalidAuthToken;
import model.game.GameData;
import model.user.AuthData;
import model.user.UserData;
import org.mindrot.jbcrypt.BCrypt;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws ResponseException {
        configureDatabase();
    }

    @Override
    public UserData addUser(UserData userInput) throws DataAccessException {
        String sql = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";

        UserData userEncrypted = storeUserPassword(userInput.username(),userInput.password(),userInput.email());

        int newId = executeUpdate(sql,
                userEncrypted.username(),
                userEncrypted.password(),
                userEncrypted.email());

        System.out.println("\n[SQL] - User Added: "+ userEncrypted.username());
        return userEncrypted;
    }



    public UserData getUser(String username) throws ResponseException {
        String sql = "SELECT username, password, email FROM userData WHERE username=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,username);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.print("\n[SQL] - getUser: "+ rs.getString("username"));
                    return readUserData(rs);
                } else {
                    System.out.println("\n[SQL] - getUser, No User Found");
                    return null;
                }
            }

        } catch(SQLException e){
            throw new ResponseException(500, "Unable to query userData: " + e.getMessage());
        }
    }

    @Override
    public void addAuthData(AuthData authData) throws ResponseException {
        String sql = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        int newId = executeUpdate(sql,
                authData.authToken(),
                authData.username()
        );
        System.out.println("\n[SQL] - authData Added for user: " + authData.username());
    }

    @Override
    public UserData getUserByAuth(String authToken) throws ResponseException {
        AuthData authData;
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT authToken, username FROM authData WHERE authToken=?")) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ResponseException(400, "[SQL] - getUserByAuth: authToken not found");
                }
                authData = readAuthData(rs);
            }
        } catch (SQLException e) {
            throw new ResponseException(500, "Unable to read data: " + e.getMessage());
        }

        UserData user = getUser(authData.username());
        System.out.println("\n[SQL] - getUserByAuth: " + user.username());
        return user;
    }


    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        System.out.println("\n[SQL] - deleteAuth: " + authToken);
        executeUpdate("DELETE FROM authData WHERE authToken = ?", authToken);
    }

    @Override
    public Collection<GameData> listGames() throws ResponseException{
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName FROM gameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(listGameDataHelper(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        System.out.println("\n[SQL] - Listing Games");
        return result;
    }

    @Override
    public GameData addGame(Integer inputGameID, GameData gameData) throws ResponseException {
        if (inputGameID == 0) {
            String sql = "INSERT INTO gameData (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
            int generatedId = executeUpdate(sql,
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game());
            System.out.println("\n[SQL] - Game Added, gameID: " + generatedId);
            return new GameData(generatedId,
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game());
        } else {
            String sql = "INSERT INTO gameData (id, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            executeUpdate(sql,
                    inputGameID,
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game());
            System.out.println("\n[SQL] - Game Added, gameID: " + inputGameID);
            return new GameData(inputGameID,
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game());
        }
    }



    @Override
    public GameData getGame(int gameID) throws ResponseException {
        String sql = "SELECT id, whiteUsername, blackUsername, gameName, game FROM gameData WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,gameID);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return readGameData(rs);
                } else {
                    return null;
                }
            }

        } catch(SQLException e){
            throw new ResponseException(500, "Unable to query userData: " + e.getMessage());
        }
    }

    @Override
    public void remGame(int gameID) throws DataAccessException{
        System.out.println("\n[SQL] - remGame: " + gameID);
        try {
            executeUpdate("DELETE FROM gameData WHERE id = ?", gameID);
        }catch(ResponseException e){
            throw new ResponseException(400,"[SQL] - remGame failed, gameID: " + gameID);
        }
    }

    @Override
    public void clearUsersAndAuth() throws ResponseException{
        executeUpdate("TRUNCATE userData");
        executeUpdate("TRUNCATE authData");
    }

    @Override
    public void clearGames() throws ResponseException {
        executeUpdate("TRUNCATE gameData");
    }

    public Collection<UserData> listUserDatas() throws ResponseException {
        var result = new ArrayList<UserData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM userData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ///result.add(readPet(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public Collection<AuthData> listAuthDatas() {
        return List.of();
    }

    @Override
    public boolean checkPassword(String username, String password) throws ResponseException {
        UserData user = getUser(username);
        return BCrypt.checkpw(password, user.password());
    }

    public void deleteUserData(Integer id) throws ResponseException {
        executeUpdate("DELETE FROM userData WHERE id=?", id);
    }

    public void deleteAllUserDatas() throws ResponseException {
        executeUpdate("TRUNCATE userData");
    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");

        return new UserData(username,password,email);
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private GameData listGameDataHelper(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String whiteUsername = rs.getString("whiteUsername");
        if (whiteUsername != null && whiteUsername.isEmpty()) {
            whiteUsername = null;
        }
        String blackUsername = rs.getString("blackUsername");
        if (blackUsername != null && blackUsername.isEmpty()) {
            blackUsername = null;
        }

        String gameName = rs.getString("gameName");
        return new GameData(id, whiteUsername, blackUsername, gameName, null);
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("id");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String game = rs.getString("game");
        return new GameData(gameID, whiteUsername,blackUsername,gameName,game);
    }

    UserData storeUserPassword(String username, String clearTextPassword, String email) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        return new UserData(username,hashedPassword,email);
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    ///else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    // Example for making a table code:
    /*
    CREATE TABLE IF NOT EXISTS  gameData (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
            `json` TEXT DEFAULT NULL,
            PRIMARY KEY (`id`),
            INDEX(type),
            INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
     */

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS userData (
      `id` int NOT NULL AUTO_INCREMENT,
      `username` varchar(256) NOT NULL,
      `password` varchar(256) NOT NULL,
      `email` varchar(256) NOT NULL,
      PRIMARY KEY (`id`),
      INDEX(username),
      INDEX(password),
      INDEX(email)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """,
            """
    CREATE TABLE IF NOT EXISTS gameData (
      `id` int NOT NULL AUTO_INCREMENT,
      `whiteUsername` varchar(256),
      `blackUsername` varchar(256),
      `gameName` varchar(256) NOT NULL,
      `game` TEXT NOT NULL,
      `json` TEXT DEFAULT NULL,
      PRIMARY KEY (`id`),
      INDEX(gameName),
      INDEX(whiteUsername),
      INDEX(blackUsername)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """,
            """
    CREATE TABLE IF NOT EXISTS authData (
      `id` int NOT NULL AUTO_INCREMENT,
      `authToken` varchar(256) NOT NULL,
      `username` varchar(256) NOT NULL,
      PRIMARY KEY (`id`),
      INDEX(authToken),
      INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };



    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}