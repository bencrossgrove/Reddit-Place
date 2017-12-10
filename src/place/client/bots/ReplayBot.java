package place.client.bots;

import place.*;
import place.network.NetworkClient;
import place.network.PlaceRequest;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * reads a log file and repeats all tile changes
 * must be run on server with dimensions 10 x 10 for optimal picture
 * and use replay.txt file in root place directory "replay.txt" as 3rd argument
 *
 * @author Ben Crossgrove
 */

public class ReplayBot extends Bot {

    private File file;
    private FileInputStream fip;

    public ReplayBot(String hostName, int portNumber, String filename) {
        super(hostName, portNumber, "replay-bot");
        this.file = new File(filename);
        Logger.log("using file " + file);
        try {
            this.fip = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open FileInputStream");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(
                    "Usage: java RandomBot <host name> <port number> <filename>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String filename = args[2];

        Bot replayBot = new ReplayBot(hostName, portNumber, filename);

        replayBot.execute();
    }

    /**
     * loop to cycle colors and place tiles in snake-like pattern
     */
    protected void changeTiles(int boardDim, String username, NetworkClient networkClient) {
        Object obj = null;
        try (
                ObjectInputStream oip = new ObjectInputStream(new BufferedInputStream(fip));
        ) {
            while (true) {
                PlaceRequest<?> request = (PlaceRequest<?>) oip.readObject();
                int maxIndex = networkClient.getModel().getPlaceBoard().DIM;
                PlaceTile current = (PlaceTile) request.getData();
                // only print if fits in current board dim
                if (current.getCol() < maxIndex && current.getRow() < maxIndex) {
                    networkClient.sendChangeTileReq(current);
                    Thread.sleep(500);
                } else {
                    System.out.println("Tile out of range: " + current);
                }
            }
        } catch (EOFException e) {
            Logger.debug("End of file reached");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                fip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

