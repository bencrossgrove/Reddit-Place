package server;

import place.PlaceBoard;
import place.PlaceBoardObservable;
import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Used to send all CHANGE_TILE requests from Client to Server
 * @author Ben Crossgrove
 */

public class ClientServerThread extends Thread {
    private ObjectInputStream in;
    private String clientName;
    private PlaceBoardObservable board;

    public ClientServerThread(ObjectInputStream in, String clientName, PlaceBoardObservable board) {
        super("ClientServerThread");
        this.in = in;
        this.clientName = clientName;
        this.board = board;
    }

    /**
     * run method of ClientServerThread to handle all CHANGE_TILE requests from a client
     */
    public void run() {
        try {
            PlaceRequest<?> placeRequest;
            while ((placeRequest = (PlaceRequest<?>) in.readObject()) != null) {
                PlaceRequest.RequestType requestType = placeRequest.getType();
                if (requestType == PlaceRequest.RequestType.CHANGE_TILE) {
                    PlaceTile tile = (PlaceTile) placeRequest.getData();
                    NetworkServer.getInstance().update(tile);
                    board.getPlaceBoard().setTile(tile);
                } else {
                    System.err.println("Unexpected request type {requestType}");
                    System.exit(1);
                }
            }
        } catch (SocketException se) {
            System.out.println(clientName + " has exited");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
