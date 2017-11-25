package place.client.ptui;

import place.PlaceBoard;
import place.PlaceBoardObservable;
import place.PlaceException;
import place.network.NetworkClient;
import place.network.PlaceRequest;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlacePTUI extends ConsoleApplication implements Observer {

    private PlaceBoardObservable model;
    private Scanner userIn;
    private PrintWriter userOut;
    private NetworkClient serverConn;

    public void init() {
        List<String> args = super.getArguments();
        if (args.size() != 3) {
            System.err.println(
                    "Usage: java PlacePTUI <host name> <port number> <username>");
            System.exit(1);
        }

        String hostName = args.get(0);
        int portNumber = Integer.parseInt(args.get(1));
        String username = args.get(2);

        System.out.println("Client connecting to " + hostName + ":" + portNumber);

        this.serverConn = new NetworkClient(hostName, portNumber, username);

        this.model = serverConn.getModel();
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
        while (!userIn.nextLine().equals("-1")) {
            try {
                this.wait();
            } catch (InterruptedException ie) {
            }
        }
    }

    private synchronized void endGame() {
        this.notify();
    }

    /**
     * Update all GUI Nodes to match the state of the model.
     */
    private void displayBoard() {
        userOut.println(this.model.toString());
    }

    /**
     * GUI is closing, so close the network connection. Server will
     * get the message.
     */
    @Override
    public void stop() {
        this.userIn.close();
        this.userOut.close();
        this.serverConn.close();
    }

    @Override
    public void update(Observable o, Object arg) {
        // refresh tiles
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
