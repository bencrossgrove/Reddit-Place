package place.server;

import place.Logger;
import place.PlaceBoardCheckout;
import place.PlaceBoardObservable;
import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.*;
import java.net.SocketException;

/**
 * Used to receive all CHANGE_TILE requests from Client to Server
 *
 * @author Ben Crossgrove
 */

public class ClientServerThread extends Thread {

    private ObjectInputStream in;
    private String clientName;
    private PlaceBoardObservable board;
    private PlaceBoardCheckout boardCheckout;

    public ClientServerThread(ObjectInputStream in, String clientName, PlaceBoardObservable board, PlaceBoardCheckout boardCheckout) {
        super("ClientServerThread");
        this.in = in;
        this.clientName = clientName;
        this.board = board;
        this.boardCheckout = boardCheckout;
        Logger.debug("ClientServerThread being created");
    }

    /**
     * run method of ClientServerThread to handle all CHANGE_TILE requests from a client
     */
    public void run() {
        Logger.debug("ClientServerThread run");
        try {
            PlaceRequest<?> placeRequest;
            while ((placeRequest = (PlaceRequest<?>) in.readObject()) != null) {
                PlaceRequest.RequestType requestType = placeRequest.getType();
                if (requestType == PlaceRequest.RequestType.CHANGE_TILE) {
                    PlaceTile tile = (PlaceTile) placeRequest.getData();
                    try {
                        boardCheckout.doWait();
                        NetworkServer.getInstance().update(tile);
                        board.getPlaceBoard().setTile(tile);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        boardCheckout.doNotify();
                    }
                } else {
                    System.err.println("Unexpected request type {requestType}");
                    System.exit(1);
                }
            }
        } catch (SocketException se) {
            PlaceServer.remove(clientName);
            NetworkServer.getInstance().remove(clientName);
            System.out.println(clientName + " has exited");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
