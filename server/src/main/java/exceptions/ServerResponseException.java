package exception;

import com.google.gson.Gson;
import exceptions.DataAccessException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ServerResponseException extends DataAccessException {
    final public int statusCode;

    public ServerResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static exception.ServerResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double)map.get("status")).intValue();
        // This is different from my other code
        // it is not the same as the one in my client.
        String message = map.get("message").toString();
        return new exception.ServerResponseException(status, message);
    }
}