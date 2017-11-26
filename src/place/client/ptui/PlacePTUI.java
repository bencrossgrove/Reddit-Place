package place.client.ptui;

import place.*;
import place.network.NetworkClient;

import java.io.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

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
//        while (!userIn.nextLine().equals("-1")) {
//            try {
//                this.wait();
//            } catch (InterruptedException ie) {
//            }
//        }
    }

    private synchronized void endGame() {
        this.notify();
    }


    /**
     * display the entire board
     */
    private void displayBoard() {
        userOut.println(this.model.toString());
    }
    private void changeTile(){
        boolean done = false;
        do {
            this.userOut.print("type move as: row column color ");
            this.userOut.flush();
            int row = this.userIn.nextInt();
            if (row == -1){
                this.stop();
                break;
            }
            int col = this.userIn.nextInt();
            int colorCode = this.userIn.nextInt();
            PlaceColor color = getColor(colorCode);
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
     * GUI is closing, so close the network connection. Server will
     * get the message.
     */
    @Override
    public void stop() {
        this.userIn.close();
        this.userOut.close();
        this.networkClient.stop();
        System.exit(0);
    }

    @Override
    public void update(Observable o, Object arg) {
        displayBoard();
    }

    /**
     * retrieve the corresponding color from PlaceColor enum
     * @param colorCode code of desired color
     * @return the fkn color
     */
    private PlaceColor getColor(int colorCode) {
        for(PlaceColor color : PlaceColor.values())
            if (color.getNumber() == colorCode) {
                return color;
            }
        return PlaceColor.WHITE;
    }

    /**
     * Launch the JavaFX GUI.
     *
     * @param args not used, here, but named arguments are passed to the GUI.
     *             <code>--host=<i>hostname</i> --port=<i>portnum</i></code>
     */
    public static void main(String[] args) throws ClassNotFoundException {
        ConsoleApplication.launch(PlacePTUI.class, args);
    }
}
