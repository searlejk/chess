package ui;

//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;

import java.util.Scanner;
import ui.EscapeSequences;
import ui.PregameClient;

import static java.awt.Color.*;

public class PregameRepl {
    private final PregameClient client;

    public PregameRepl(String serverUrl) {
        client = new PregameClient(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to the 240 Chess. Type help to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

}