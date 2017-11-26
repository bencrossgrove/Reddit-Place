package place.network;

import place.PlaceBoard;
import place.PlaceTile;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Used to pass requests to the server
 * @author Ben Crossgrove
 */

public class PlaceExchange {

    /**
     * handle LOGIN requests
     * @param out stream to send to server
     * @param client user who sent request
     * @throws IOException when something goes wrong with the stream
     */
    public static void login(ObjectOutputStream out, String client) throws IOException {
        PlaceRequest<String> request = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, client);
        out.writeObject(request);
    }

    /**
     * handle LOGIN_SUCCESS requests
     * @param out stream to send to server
     * @param client user who is now logged in
     * @throws IOException when something goes wrong with the stream
     */
    public static void loginSuccess(ObjectOutputStream out, String client) throws IOException {
        PlaceRequest<String> request = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS, client);
        out.writeObject(request);
    }

    /**
     * handle BOARD requests
     * @param out stream to send to server
     * @param board the board to be used
     * @throws IOException when something goes wrong with the stream
     */
    public static void board(ObjectOutputStream out, PlaceBoard board) throws IOException {
        PlaceRequest<PlaceBoard> request = new PlaceRequest<PlaceBoard>(PlaceRequest.RequestType.BOARD, board);
        out.writeObject(request);
    }

    /**
     * handle CHANGE_TILE requests
     * @param out stream to send to server
     * @param tile the changed tile
     * @throws IOException when something goes wrong with the stream
     */
    public static void changeTile(ObjectOutputStream out, PlaceTile tile) throws IOException {
        PlaceRequest<PlaceTile> request = new PlaceRequest<PlaceTile>(PlaceRequest.RequestType.CHANGE_TILE, tile);
        out.writeObject(request);
    }

    /**
     * handle TILE_CHANGED requests
     * @param out stream to send to server
     * @param tile the changed tile
     * @throws IOException when something goes wrong with the stream
     */
    public static void tileChanged(ObjectOutputStream out, PlaceTile tile) throws IOException {
        PlaceRequest<PlaceTile> request = new PlaceRequest<PlaceTile>(PlaceRequest.RequestType.TILE_CHANGED, tile);
        out.writeObject(request);
    }

}
