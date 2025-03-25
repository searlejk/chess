package ui;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class PreLoginRepl {
    private final PreLoginClient client;
    private final String serverUrl;

    public PreLoginRepl(String serverUrl) {
        this.serverUrl = serverUrl;
        client = new PreLoginClient(serverUrl);
    }

    public void run() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.println("\nWelcome to the 240 Chess. Type help to get started.");
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
                    new LoginRepl(this.serverUrl, client.authToken).run();
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
        System.out.print("\n"+ SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_RED + "[" +
    EscapeSequences.SET_TEXT_COLOR_WHITE + "LOGGED_OUT" + EscapeSequences.SET_TEXT_COLOR_RED + "]" +
    EscapeSequences.SET_TEXT_COLOR_WHITE + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC );
    }

}