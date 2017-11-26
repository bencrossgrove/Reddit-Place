package server;

import place.PlaceBoard;
import place.PlaceBoardObservable;
import place.network.PlaceExchange;
import place.network.PlaceRequest;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceServer {

    /**
     * static list of users that are successfully logged in to the server
     */
    private static List<String> users = new ArrayList<>();

    /**
     * Turn on if standard output debug messages are desired.
     */
    private static final boolean DEBUG = true;

    /**
     * Print method that does something only if DEBUG is true
     *
     * @param logMsg the message to log
     */
    private static void dPrint( Object logMsg ) {
        if ( PlaceServer.DEBUG ) {
            System.out.println( logMsg );
        }
    }

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
        NetworkServer netServer = NetworkServer.getInstance();
        PlaceBoardObservable model = new PlaceBoardObservable(boardDim);

        try {
            ServerSocket serverSocket =
                    new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                PlaceRequest<?> input;
                dPrint(clientSocket);
                if ((input = (PlaceRequest<?>) in.readObject()) != null) {
                    if (input.getType() == PlaceRequest.RequestType.LOGIN) {
                        String username = (String) input.getData();
                        dPrint(username);
                        if (users.contains(username)) {
                            out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.ERROR, "User " + username + " already exists"));
                        } else {
                            users.add(username);
                            netServer.add(username, out);
                            // send login success
                            PlaceExchange.loginSuccess(out, "Login of \'" + username + "\' was successful");
//                            out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN_SUCCESS, "Login of \'" + username + "\' was successful"));
                            // send board
                            PlaceExchange.board(out, model.getPlaceBoard());
//                            out.writeObject(new PlaceRequest<PlaceBoard>(PlaceRequest.RequestType.BOARD, model.getPlaceBoard()));
                            // start thread
                            //System.out.println(clientSocket.toString());
                            new ClientServerThread(in, username).start();
                        }
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
