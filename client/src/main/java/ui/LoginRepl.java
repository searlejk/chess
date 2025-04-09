package ui;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class LoginRepl {
    private final LoginClient client;
    private final String serverUrl;
    private final String authToken;

    public LoginRepl(String serverUrl, String authToken) {
        this.serverUrl = serverUrl;
        client = new LoginClient(serverUrl, authToken);
        this.authToken = authToken;
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

                if (client.state==State.LOGGEDOUT){
                    new PreLoginRepl(this.serverUrl).run();
                    break;
                }

                if (client.state==State.INGAME1){
                    new GameRepl(this.serverUrl, authToken, 1, client.gameID, client.ws).run();
                    break;
                }

                if (client.state==State.INGAME2){
                    new GameRepl(this.serverUrl, authToken, 2, client.gameID, client.ws).run();
                    break;
                }

                if (client.state==State.OBSERVING){
                    new GameRepl(this.serverUrl, authToken, 3, client.gameID, client.ws).run();
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
        System.out.print("\n" + SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_GREEN + "[" +
                EscapeSequences.SET_TEXT_COLOR_WHITE + "LOGGED_IN" + EscapeSequences.SET_TEXT_COLOR_GREEN + "]" +
                EscapeSequences.SET_TEXT_COLOR_WHITE + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC);

    }

}