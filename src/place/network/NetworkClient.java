package place.network;

import place.PlaceBoard;
import place.PlaceBoardObservable;
import place.PlaceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NetworkClient {

    /**
     * used to communicate with the reversi server.
     */
    private Socket socket;

    /**
     * used to read requests from the reversi server.
     */
    private ObjectInputStream networkIn;

    /**
     * used to write responses to the reversi server.
     */
    private ObjectOutputStream networkOut;

    /**
     * used to keep track of the state of the game.
     */
    private PlaceBoardObservable model;

    /**
     * used to control the main game loop.
     */
    private boolean go;

    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean running() {
        return this.go;
    }

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop() {
        this.go = false;
    }


    public NetworkClient(String hostName, int portNumber, String username) {
        try {
            socket = new Socket(hostName, portNumber);
            networkOut = new ObjectOutputStream(socket.getOutputStream());
            networkIn = new ObjectInputStream(socket.getInputStream());

            PlaceRequest<String> loginRequest = new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN, username);
            networkOut.writeObject(loginRequest);
            // login request's response
            PlaceRequest<?> response = (PlaceRequest<?>) networkIn.readObject();
            assert response.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS :
                    "Login was unsuccessful";
            // get the board from the server
            PlaceRequest<?> boardRes = (PlaceRequest<?>) networkIn.readObject();
            assert boardRes.getType() == PlaceRequest.RequestType.BOARD :
                    "Did not receive PlaceBoard";
            // create instance of board
            PlaceBoard board = (PlaceBoard) boardRes.getData();
            this.model = new PlaceBoardObservable(board);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    /**
     * main run method
     */
    public void run() {

    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    public void close() {
        try {
            this.socket.close();
        } catch (IOException ioe) {
            // squash
        }
    }

    public PlaceBoardObservable getModel() {
        return model;
    }

}
