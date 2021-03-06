package place.server;

import place.Logger;
import place.PlaceTile;
import place.network.PlaceExchange;

import java.io.*;
import java.net.SocketException;
import java.util.*;

/**
 * Handles TILE_CHANGED requests to be sent to all clients
 * Singleton
 *
 * @author Ben Crossgrove
 */

public class NetworkServer {

    private static NetworkServer server;

    private Map<String, ObjectOutputStream> users = new HashMap<String, ObjectOutputStream>() {
    };
    private File file;
    private ObjectOutputStream oos;

    private NetworkServer() {
        super();
        this.file = new File("src/place/tile-changes.txt");
        try {
            this.oos = new ObjectOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            this.oos = null;
        }
    }

    /**
     * get instance of the network server
     *
     * @return the network server (singleton)
     */
    public static NetworkServer getInstance() {
        if (server == null) {
            server = new NetworkServer();
        }
        return server;
    }

    // add a user to the network place.server
    public void add(String username, ObjectOutputStream stream) {
        users.put(username, stream);
    }

    // remove a user from server
    public void remove(String username) {
        users.remove(username);
    }

    // send tile changed to all users output streams
    public void update(PlaceTile tile) throws IOException {
        Logger.debug("NetworkServer update " + tile);
        List<String> toRemove = new ArrayList<>();
        for (String user : users.keySet()) {
            try {
                ObjectOutputStream output = users.get(user);
                PlaceExchange.tileChanged(output, tile);
            } catch (SocketException se) {
                // add to list of sockets to be removed due to logoff from client
                toRemove.add(user);
            }
        }
        // logs tile changes to file
        logToFile(tile);
        // added to prevent ConcurrentModificationException, cannot remove from list during iteration!
        for (String removeUser : toRemove) {
            remove(removeUser);
        }
    }

    /**
     * send tile changed requests to file
     *
     * @param tile tile to send
     * @throws IOException if something goes wrong with file
     */
    private void logToFile(PlaceTile tile) throws IOException {
        if (oos != null) {
            PlaceExchange.tileChanged(oos, tile);
        }
    }

    /**
     * close the stream
     */
    public void close() {
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
