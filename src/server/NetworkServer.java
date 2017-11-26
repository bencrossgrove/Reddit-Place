package server;

import place.PlaceTile;
import place.network.PlaceExchange;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.*;

public class NetworkServer {

    private static NetworkServer server;

    private Map<String, ObjectOutputStream> users = new HashMap<String, ObjectOutputStream>() {
    };

    private NetworkServer() {
        super();
    }

    public static NetworkServer getInstance() {
        if (server == null) {
            server = new NetworkServer();
        }
        return server;
    }

    // add a user to the network server
    public void add(String username, ObjectOutputStream stream) {
        users.put(username, stream);
    }

    private void remove(String username) {
        users.remove(username);
    }

    // send tile changed to all users output streams
    public void update(PlaceTile tile) throws IOException {
        List<String> toRemove = new ArrayList<>();
        for (String user : users.keySet()){
            try {
                ObjectOutputStream output = users.get(user);
                PlaceExchange.tileChanged(output, tile);
            } catch (SocketException se){
                toRemove.add(user);
            }
        }
        // added to prevent ConcurrentModificationException, cannot remove from list during iteration!
        for (String removeUser : toRemove){
            remove(removeUser);
        }
    }

}
