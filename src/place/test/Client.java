package place.test;

import place.network.PlaceRequest;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        if (args.length != 3) {
            System.err.println(
                    "Usage: java Client <host name> <port number> <username>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String username = args[2];

        System.out.println("Client connecting to " + hostName + ":" + portNumber);

        try (
                Socket client = new Socket(hostName, portNumber);
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
        ) {
            boolean listening = true;
            PlaceRequest<String> loginRequest = new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN, args[2]);
            out.writeObject(loginRequest);
            while (listening) {
                PlaceRequest<?> response = (PlaceRequest<?>) in.readObject();
                if (response.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS){
                    System.out.println("success");
                } else if (response.getType() == PlaceRequest.RequestType.ERROR){
                    System.out.println("fail");
                    listening = false;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}