package place.network;

import place.PlaceBoard;
import place.PlaceBoardObservable;
import place.PlaceTile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
    public synchronized void stop() {
        this.go = false;
    }


    public NetworkClient(String hostName, int portNumber, String username) {
        try {
            this.socket = new Socket(hostName, portNumber);
            this.networkOut = new ObjectOutputStream(socket.getOutputStream());
            this.networkIn = new ObjectInputStream(socket.getInputStream());
            this.go = true;

            PlaceExchange.login(networkOut, username);
//            PlaceRequest<String> loginRequest = new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN, username);
//            networkOut.writeObject(loginRequest);
            // login request's response
            PlaceRequest<?> response = (PlaceRequest<?>) networkIn.readObject();
            // check if login successful
            if (response.getType() != PlaceRequest.RequestType.LOGIN_SUCCESS) {
                System.out.println(response.getData());
                System.exit(1);
            }
            // print login success message
            System.out.println(response.getData());
            // get the board from the server
            PlaceRequest<?> boardRes = (PlaceRequest<?>) networkIn.readObject();
            // check if board was sent
            if (boardRes.getType() != PlaceRequest.RequestType.BOARD) {
                System.err.println("BOARD NOT RECEIVED: Expected response BOARD got " + response.getType());
                System.exit(1);
            }
            // create instance of board
            PlaceBoard board = (PlaceBoard) boardRes.getData();
            this.model = new PlaceBoardObservable(board);
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
     * send a change tile request to the server
     */
    public void sendChangeTileReq(PlaceTile tile) {
        try {
            PlaceExchange.changeTile(networkOut, tile);
//            PlaceRequest<PlaceTile> tileChange = new PlaceRequest<PlaceTile>(PlaceRequest.RequestType.CHANGE_TILE, tile);
//            this.networkOut.writeObject(tileChange);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * main run method
     */
    private void run() {
        while (this.running()) {
            try {
                PlaceRequest<?> request = (PlaceRequest<?>) this.networkIn.readObject();
                PlaceRequest.RequestType requestType = request.getType();
                if (requestType == PlaceRequest.RequestType.TILE_CHANGED) {
                    this.model.setTile((PlaceTile) request.getData());
                } else {
                    System.err.println("Bad request: " + requestType);
                    this.stop();
                }
            } catch (ClassNotFoundException | IOException e) {
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
