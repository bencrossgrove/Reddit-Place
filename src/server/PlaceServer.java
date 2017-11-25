package server;

import place.PlaceBoard;
import place.PlaceBoardObservable;
import place.network.PlaceRequest;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceServer {

    private static List<String> users = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 2) {
            System.err.println("Usage: java Server <port number> <board dimension");
            System.exit(1);
        } else if (Integer.parseInt(args[1]) < 1) {
            System.err.println("Board dimension must be >= 1");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int boardDim = Integer.parseInt(args[1]);

        while (true) {
            try (
                    ServerSocket serverSocket =
                            new ServerSocket(portNumber);
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream out =
                            new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ) {
                PlaceRequest<?> input;
                if ((input = (PlaceRequest<?>) in.readObject()) != null) {
                    if (input.getType() == PlaceRequest.RequestType.LOGIN) {
                        String username = (String) input.getData();
                        if (users.contains(username)) {
                            out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.ERROR, "User already exists"));
                        } else {
                            users.add(username);
                            PlaceBoardObservable model = new PlaceBoardObservable(boardDim);
                            // send login success
                            out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN_SUCCESS, "Login successful"));
                            // send board
                            out.writeObject(new PlaceRequest<PlaceBoard>(PlaceRequest.RequestType.BOARD, model.getPlaceBoard()));
                            // start thread
                            System.out.println(clientSocket.toString());
                            new ClientServerThread(clientSocket, username).start();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                        + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }

    }
}
