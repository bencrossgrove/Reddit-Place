package place.client.bots;

import place.PlaceBoardObservable;
import place.network.NetworkClient;

public class Bot {

    public static NetworkClient setup(String hostName, int portNumber, String username) {
        return new NetworkClient(hostName, portNumber, username);
    }

}
