package place.client.bots;

import place.*;
import place.network.NetworkClient;

import java.util.concurrent.ThreadLocalRandom;

/**
 * a bot that cycles through all colors traveling in a snake-like pattern
 *
 * @author Ben Crossgrove
 */

public class ColorCycleBot extends Bot {

    public ColorCycleBot(String hostName, int portNumber, String username){
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

        Bot colorCycleBot = new ColorCycleBot(hostName, portNumber, username);

        colorCycleBot.execute();
    }

    /**
     * loop to cycle colors and place tiles in snake-like pattern
     */
    protected void changeTiles(int boardDim, String username, NetworkClient networkClient) {
        int x = 0;
        int y = (boardDim - 1);
        int col = x;
        int switchInt = 1;
        try {
            while (true) {
                for (int c = 0; c < PlaceColor.TOTAL_COLORS; c++) {
                    for (int row = 0; row < boardDim; row++) {
                        while (true) {
                            PlaceColor color = PlaceColorUtil.getColor(c);
                            PlaceTile selectedTile = new PlaceTile(row, col, username, color, System.currentTimeMillis());
                            networkClient.sendChangeTileReq(selectedTile);
                            try {
                                Thread.sleep(500);
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
        } catch (Exception e) {
            System.out.println("Bot terminated");
        }

    }

}
