package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import exceptions.DataAccessException;
import exceptions.InvalidAuthToken;
import model.game.GameData;
import model.user.AuthData;
import model.user.UserData;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws ResponseException {
        configureDatabase();
    }

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        int newId = executeUpdate(sql,
                user.username(),
                user.password(),
                user.email());

        System.out.println("[SQL] - User Added: "+ user.username());
        return user;
    }



    public UserData getUser(String username) throws ResponseException {
        String sql = "SELECT username, password, email FROM userData WHERE username=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,username);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                } else {
                    return null;
                }
            }

        } catch(SQLException e){
            throw new ResponseException(500, "Unable to query userData: " + e.getMessage());
        }
    }

    @Override
    public void addAuthData(AuthData authData) throws ResponseException {

    }

    @Override
    public UserData getUserByAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData addGame(Integer inputGameID, GameData gameData) {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void remGame(int gameID) {

    }

    @Override
    public void clearUsersAndAuth() {

    }

    @Override
    public void clearGames() throws ResponseException {
        var statement = "TRUNCATE gameData";
        executeUpdate(statement);
    }

    public UserData getUser(int id) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM userData WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ///return readPet(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
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

    public void deleteUserData(Integer id) throws ResponseException {
        var statement = "DELETE FROM userData WHERE id=?";
        executeUpdate(statement, id);
    }

    public void deleteAllUserDatas() throws ResponseException {
        var statement = "TRUNCATE userData";
        executeUpdate(statement);
    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
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
      `gameID` int NOT NULL,
      `whiteUsername` varchar(256) NOT NULL,
      `blackUsername` varchar(256) NOT NULL,
      `gameName` varchar(256) NOT NULL,
      `game` varchar(256) NOT NULL,
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
      `json` TEXT DEFAULT NULL,
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