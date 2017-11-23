package place.test;

import place.network.PlaceRequest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

    private static List<Object> users = new ArrayList<>();

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length != 2) {
            System.err.println("Usage: java Server <port number> <board dimension");
            System.exit(1);
        } else if (Integer.parseInt(args[1]) < 1) {
            System.err.println("Board dimension must be >= 1");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int boardDim = Integer.parseInt(args[1]);

        try (
                ServerSocket serverSocket =
                        new ServerSocket(Integer.parseInt(args[0]));
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            PlaceRequest<?> input;
            while ((input = (PlaceRequest<?>) in.readObject()) != null) {
                if (input.getType() == PlaceRequest.RequestType.LOGIN){
                    if (users.contains(input.getData())){
                        out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.ERROR, "User already exists"));
                    } else {
                        users.add(input.getData());
                        out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN_SUCCESS, "Login successful"));
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
