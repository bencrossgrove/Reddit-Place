package server;

import place.network.PlaceRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientServerThread extends Thread {
    private Socket socket = null;
    private String clientName;

    public ClientServerThread(Socket socket, String clientName) {
        super("ClientServerThread");
        this.socket = socket;
        this.clientName = clientName;
    }

    public void run() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            PlaceRequest<?> inputLine, outputLine;
            while ((inputLine = (PlaceRequest<?>) in.readObject()) != null) {
                PlaceRequest.RequestType inputType = inputLine.getType();
                if (inputType == PlaceRequest.RequestType.BOARD) {
                    System.out.println("board");
                } else if (inputType == PlaceRequest.RequestType.CHANGE_TILE) {

                } else if (inputType == PlaceRequest.RequestType.TILE_CHANGED) {

                } else {
                    System.err.println("Unexpected request type {inputType}");
                    System.exit(1);
                }
            }
            //socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
