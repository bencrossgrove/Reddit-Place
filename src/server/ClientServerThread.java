package server;

import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientServerThread extends Thread {
    private ObjectInputStream in;
    private String clientName;

    public ClientServerThread(ObjectInputStream in, String clientName) {
        super("ClientServerThread");
        this.in = in;
        this.clientName = clientName;
    }

    public void run() {
        try {
            PlaceRequest<?> placeRequest;
            while ((placeRequest = (PlaceRequest<?>) in.readObject()) != null) {
                PlaceRequest.RequestType requestType = placeRequest.getType();
                if (requestType == PlaceRequest.RequestType.CHANGE_TILE) {
                    PlaceTile tile = (PlaceTile) placeRequest.getData();
                    NetworkServer.getInstance().update(tile);
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
