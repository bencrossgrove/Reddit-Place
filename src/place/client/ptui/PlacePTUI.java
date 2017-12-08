package place.client.ptui;

import place.*;
import place.network.NetworkClient;

import java.io.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * Plain Text UI version of Place
 * @author Ben Crossgrove
 */

public class PlacePTUI extends ConsoleApplication implements Observer {

    private PlaceBoardObservable model;
    private Scanner userIn;
    private PrintWriter userOut;
    private NetworkClient networkClient;
    private String username;

    public void init() {
        List<String> args = super.getArguments();
        if (args.size() != 3) {
            System.err.println(
                    "Usage: java PlacePTUI <host name> <port number> <username>");
            System.exit(1);
        }

        String hostName = args.get(0);
        int portNumber = Integer.parseInt(args.get(1));
        username = args.get(2);

        System.out.println("Client connecting to " + hostName + ":" + portNumber + "\n");

        this.networkClient = new NetworkClient(hostName, portNumber, username);

        this.model = networkClient.getModel();
    }

    @Override
    public synchronized void go(Scanner keyboardIn, PrintWriter consoleOut) {
        this.userIn = keyboardIn;
        this.userOut = consoleOut;
        // Connect UI to model. Can't do it sooner because streams not set up.
        this.model.addObserver(this);
        // Manually force a display of all board state, since it's too late
        // to trigger update().
        this.displayBoard();
        this.changeTile();
    }


    /**
     * display the entire board
     */
    private void displayBoard() {
        userOut.println(this.model.toString());
    }

    /**
     * Where user input is accepted and sent as a new tile to the NetworkClient where the CHANGE_TILE request
     * is then sent to the place.server
     */
    private void changeTile() {
        boolean done = false;
        do {
            this.userOut.print("type move as: row column color ");
            this.userOut.flush();
            int row = this.userIn.nextInt();
            // exit condition -1, close streams and socket then exit by calling stop()
            if (row == -1) {
                this.stop();
                break;
            }
            int col = this.userIn.nextInt();
            int colorCode = this.userIn.nextInt();
            PlaceColor color = PlaceColorUtil.getColor(colorCode);
            PlaceTile selectedTile = new PlaceTile(row, col, username, color, System.currentTimeMillis());
            if (this.model.isValid(selectedTile)) {
                this.userOut.println(selectedTile);
                this.networkClient.sendChangeTileReq(selectedTile);
            } else {
                System.out.println("Pick a different tile...this one doesn't exist!");
            }
        } while (!done);
    }

    /**
     * PTUI is closing, so close the network connection. Server will
     * get the message.
     */
    @Override
    public void stop() {
        this.userIn.close();
        this.userOut.close();
        this.networkClient.stop();
        System.exit(0);
    }

    /**
     * display the board after being notified of a changed tile
     *
     * @param o   PlaceBoardObservable model
     * @param arg tile that was changed
     */
    @Override
    public void update(Observable o, Object arg) {
        displayBoard();
    }

    /**
     * Start new console application
     */
    public static void main(String[] args) throws ClassNotFoundException {
        ConsoleApplication.launch(PlacePTUI.class, args);
    }
}
