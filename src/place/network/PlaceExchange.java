package place.network;

import place.PlaceBoard;
import place.PlaceTile;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class PlaceExchange {

    public static void login(ObjectOutputStream out, String client) throws IOException {
        PlaceRequest<String> request = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, client);
        out.writeObject(request);
    }

    public static void loginSuccess(ObjectOutputStream out, String client) throws IOException {
        PlaceRequest<String> request = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS, client);
        out.writeObject(request);
    }

    public static void board(ObjectOutputStream out, PlaceBoard board) throws IOException {
        PlaceRequest<PlaceBoard> request = new PlaceRequest<PlaceBoard>(PlaceRequest.RequestType.BOARD, board);
        out.writeObject(request);
    }

    public static void changeTile(ObjectOutputStream out, PlaceTile tile) throws IOException {
        PlaceRequest<PlaceTile> request = new PlaceRequest<PlaceTile>(PlaceRequest.RequestType.CHANGE_TILE, tile);
        out.writeObject(request);
    }

    public static void tileChanged(ObjectOutputStream out, PlaceTile tile) throws IOException {
        PlaceRequest<PlaceTile> request = new PlaceRequest<PlaceTile>(PlaceRequest.RequestType.TILE_CHANGED, tile);
        out.writeObject(request);
    }

}
