package place.client.bots;

import place.*;
import place.network.NetworkClient;

import java.util.concurrent.ThreadLocalRandom;

/**
 * a bot that sets tiles of random color and coordinates on the board
 *
 * @author Ben Crossgrove
 */

public class ColorCycleBot extends Bot {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(
                    "Usage: java RandomBot <host name> <port number> <username>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String username = args[2];

        System.out.println("Client connecting to " + hostName + ":" + portNumber + "\n");

        NetworkClient networkClient = Bot.setup(hostName, portNumber, username);

        PlaceBoardObservable model = networkClient.getModel();
        int boardDim = model.getPlaceBoard().DIM;

        changeTiles(boardDim, username, networkClient);
    }


    /**
     * Where user input is accepted and sent as a new tile to the NetworkClient where the CHANGE_TILE request
     * is then sent to the place.server
     */
    private static void changeTiles(int boardDim, String username, NetworkClient networkClient) {
        int x = 0;
        int y = (boardDim - 1);
        int col = x;
        int switchInt = 1;
        while (true) {
            for (int c = 0; c < PlaceColor.TOTAL_COLORS; c++) {
                for (int row = 0; row < boardDim; row++) {
                    while (true) {
                        PlaceColor color = PlaceColorUtil.getColor(c);
                        PlaceTile selectedTile = new PlaceTile(row, col, username, color, System.currentTimeMillis());
                        networkClient.sendChangeTileReq(selectedTile);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        col += switchInt;
                        if (col == boardDim || col == -1) {
                            switchInt *= -1;
                            col += switchInt;
                            break;
                        }
                    }
                }
            }
        }
    }

}
