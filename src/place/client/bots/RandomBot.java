package place.client.bots;

import place.*;
import place.network.NetworkClient;

import java.util.concurrent.ThreadLocalRandom;

/**
 * a bot that sets tiles of random color and coordinates on the board
 *
 * @author Ben Crossgrove
 */

public class RandomBot extends Bot {

    public RandomBot(String hostName, int portNumber, String username) {
        super(hostName, portNumber, username);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(
                    "Usage: java RandomBot <host name> <port number> <username>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String username = args[2];

        Bot randomBot = new RandomBot(hostName, portNumber, username);

        randomBot.execute();
    }


    /**
     * loop that places tiles of random color and coordinate on board
     */
    protected void changeTiles(int boardDim, String username, NetworkClient networkClient) {
        boolean done = false;
        try {
            while (true) {
                int row = ThreadLocalRandom.current().nextInt(0, boardDim);
                int col = ThreadLocalRandom.current().nextInt(0, boardDim);
                int colorCode = ThreadLocalRandom.current().nextInt(0, PlaceColor.TOTAL_COLORS);
                PlaceColor color = PlaceColorUtil.getColor(colorCode);
                PlaceTile selectedTile = new PlaceTile(row, col, username, color, System.currentTimeMillis());
                networkClient.sendChangeTileReq(selectedTile);
                Logger.debug(selectedTile.toString());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Bot terminated");
        }
    }
}
