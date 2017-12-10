package place.network;

import place.Logger;
import place.PlaceBoard;
import place.PlaceBoardObservable;
import place.PlaceTile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Network interface for client, handles both read and write actions
 * Controller in MVC
 *
 * @author Ben Crossgrove
 */

public class NetworkClient {

    /**
     * used to communicate with the reversi place.server.
     */
    private Socket socket;

    /**
     * used to read requests from the place.server.
     */
    private ObjectInputStream networkIn;

    /**
     * used to write responses to the place.server.
     */
    private ObjectOutputStream networkOut;

    /**
     * used to keep track of the state of the board.
     */
    private PlaceBoardObservable model;

    /**
     * used to control the main loop.
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
    public synchronized void stop() {
        this.go = false;
    }

    /**
     * Constructor that handles login and receives board then spawns thread to handle client actions
     *
     * @param hostName   client hostname
     * @param portNumber client port number
     * @param username   client username
     */
    public NetworkClient(String hostName, int portNumber, String username) {
        Logger.debug("NetworkClient constructor");
        try {
            this.socket = new Socket(hostName, portNumber);
            this.networkOut = new ObjectOutputStream(socket.getOutputStream());
            this.networkIn = new ObjectInputStream(socket.getInputStream());
            this.go = true;

            // send login request
            PlaceExchange.login(networkOut, username);
            // login request's response
            PlaceRequest<?> response = (PlaceRequest<?>) networkIn.readObject();
            // check if login successful
            if (response.getType() != PlaceRequest.RequestType.LOGIN_SUCCESS) {
                System.out.println(response.getData());
                System.exit(1);
            }
            // print login success message
            Logger.log((String) response.getData());
            // get the board from the place.server
            PlaceRequest<?> boardRes = (PlaceRequest<?>) networkIn.readObject();
            // check if board was sent
            if (boardRes.getType() != PlaceRequest.RequestType.BOARD) {
                System.err.println("BOARD NOT RECEIVED: Expected response BOARD got " + response.getType());
                System.exit(1);
            }
            // create instance of board
            PlaceBoard board = (PlaceBoard) boardRes.getData();
            this.model = new PlaceBoardObservable(board);
            // handle rest of client in separate thread
            Logger.debug("NetworkClient spawning thread");
            Thread netThread = new Thread(this::run);
            netThread.start();
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
     * send a change tile request to the place.server
     */
    public void sendChangeTileReq(PlaceTile tile) {
        try {
            PlaceExchange.changeTile(networkOut, tile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * main run method
     */
    private void run() {
        Logger.debug("NetworkClient run()");
        while (this.running()) {
            try {
                PlaceRequest<?> request = (PlaceRequest<?>) this.networkIn.readObject();
                PlaceRequest.RequestType requestType = request.getType();
                if (requestType == PlaceRequest.RequestType.TILE_CHANGED) {
                    this.model.setTile((PlaceTile) request.getData());
                    Thread.sleep(500); /*wait to prevent sending another tile in rapid fashion*/
                } else {
                    System.err.println("Bad request: " + requestType);
                    this.stop();
                }
            } catch (ClassNotFoundException | IOException | InterruptedException e) {
                e.printStackTrace();
                this.stop();
            }
        }
        this.close();
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
