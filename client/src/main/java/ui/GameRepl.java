package ui;

import websocket.WebSocketFacade;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class GameRepl {
    private final GameClient client;
    private final String serverUrl;
    private final String authToken;
    private WebSocketFacade ws;

    public GameRepl(String serverUrl, String authToken, int side, int gameID, WebSocketFacade ws) {
        this.serverUrl = serverUrl;
        client = new GameClient(serverUrl, authToken, side, gameID);
        this.authToken = authToken;
        this.ws = ws;
    }

    public void run() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);

                if (client.state==State.LOGGEDIN){
                    new LoginRepl(this.serverUrl, authToken).run();
                    break;
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (client.observing) {
            System.out.print("\n" + SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_MAGENTA + "[" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + "OBSERVING" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + "]" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC);
        } else {
            System.out.print("\n" + SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "[" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + "IN_GAME" + EscapeSequences.SET_TEXT_COLOR_BLUE + "]" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC);
        }
    }
}