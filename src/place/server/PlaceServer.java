package place.server;

import place.Logger;
import place.PlaceBoardCheckout;
import place.PlaceBoardObservable;
import place.network.PlaceExchange;
import place.network.PlaceRequest;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates socket to accept clients as well as NetworkServer, handles LOGIN requests and creates initial board to be
 * used, sends board to clients and spawns new ClientServerThread for each client.
 * @author Ben Crossgrove
 */

public class PlaceServer {

    /**
     * static list of users that are successfully logged in to the place.server
     */
    private static List<String> users = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 2) {
            System.err.println("Usage: java Server <port number> <board dimension>");
            System.exit(1);
        } else if (Integer.parseInt(args[1]) < 1) {
            System.err.println("Board dimension must be >= 1");
            System.exit(1);
        }

        Logger.debug("Entered PlaceServer main");

        int portNumber = Integer.parseInt(args[0]);
        int boardDim = Integer.parseInt(args[1]);
        NetworkServer netServer = NetworkServer.getInstance();
        PlaceBoardObservable model = new PlaceBoardObservable(boardDim);
        PlaceBoardCheckout boardCheckout = new PlaceBoardCheckout(model);
        // so first user gets right in
        boardCheckout.doNotify();

        try {
            ServerSocket serverSocket =
                    new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.debug("Socket accepted");
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                PlaceRequest<?> input;
                if ((input = (PlaceRequest<?>) in.readObject()) != null) {
                    if (input.getType() == PlaceRequest.RequestType.LOGIN) {
                        String username = (String) input.getData();
                        if (users.contains(username)) {
                            Logger.debug("Cannot log in " + username);
                            out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.ERROR, "User " + username + " already exists"));
                        } else {
                            Logger.debug("User " + username + " can be logged in");
                            // track users successfully logged in by adding username
                            users.add(username);
                            netServer.add(username, out);
                            // send login success
                            PlaceExchange.loginSuccess(out, "Login of \'" + username + "\' was successful");
                            try {
                                boardCheckout.doWait();
                                // send board
                                PlaceExchange.board(out, model.getPlaceBoard());
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                System.out.println("Thread.sleep() was interrupted");
                                e.printStackTrace();
                            } finally {
                                boardCheckout.doNotify();
                            }
                            // start thread
                            new ClientServerThread(in, username, model, boardCheckout).start();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        } finally {
            netServer.close();
        }

    }

    public static synchronized void remove(String username) {
        users.remove(username);
    }

}
