package place.client.bots;

import place.PlaceBoardObservable;
import place.network.NetworkClient;

/**
 * setup network client for Bot and execute changeTiles for corresponding bot
 *
 * @author Ben Crossgrove
 */

public abstract class Bot {

    private String hostName;
    private int portNumber;
    private String username;

    public Bot(String hostName, int portNumber, String username) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.username = username;
    }

    /**
     * create network client and execute bot's changeTiles method
     */
    public void execute() {
        NetworkClient networkClient = setup(hostName, portNumber, username);
        PlaceBoardObservable model = networkClient.getModel();
        int boardDim = model.getPlaceBoard().DIM;
        changeTiles(boardDim, username, networkClient);
        networkClient.close();
    }

    /**
     * creates a new network client
     * @param hostName
     * @param portNumber
     * @param username
     * @return new network client instance
     */
    private NetworkClient setup(String hostName, int portNumber, String username) {
        System.out.println("Client connecting to " + hostName + ":" + portNumber + "\n");

        return new NetworkClient(hostName, portNumber, username);
    }

    /**
     * bot sub-classes must implement changeTiles which corresponds to their specific action set
     * @param boardDim board's dimension
     * @param username user
     * @param networkClient user's network client
     */
    protected abstract void changeTiles(int boardDim, String username, NetworkClient networkClient);
}
