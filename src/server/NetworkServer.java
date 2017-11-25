package server;

import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.ObjectOutputStream;
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

    // send tile changed to all users output streams
    public void update(PlaceTile tile) throws IOException {
        for (ObjectOutputStream output : users.values()){
            output.writeObject(new PlaceRequest<PlaceTile>(PlaceRequest.RequestType.TILE_CHANGED, tile));
        }
    }

}
