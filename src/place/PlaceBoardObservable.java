package place;

import java.util.Observable;

public class PlaceBoardObservable extends Observable {

    PlaceBoard placeBoard;

    public PlaceBoardObservable(PlaceBoard board) {
        Logger.debug("PlaceBoardObservable constructor board");
        this.placeBoard = board;
    }

    public PlaceBoardObservable(int DIM) {
        Logger.debug("PlaceBoardObservable constructor " + DIM);
        this.placeBoard = new PlaceBoard(DIM);
    }

    public PlaceBoard getPlaceBoard() {
        return placeBoard;
    }

    /**
     * Get the entire board.
     *
     * @return the board
     */
    public PlaceTile[][] getBoard() {
        return placeBoard.getBoard();
    }

    /**
     * Get a tile on the board
     *
     * @param row row
     * @param col column
     * @return the tile
     * @rit.pre row and column constitute a valid board coordinate
     */
    public PlaceTile getTile(int row, int col) {
        return placeBoard.getTile(row, col);
    }

    /**
     * Change a tile in the board.
     *
     * @param tile the new tile
     */
    public void setTile(PlaceTile tile) {
        Logger.debug("PlaceBoardObservable setTile " + tile);
        placeBoard.setTile(tile);
        super.setChanged();
        super.notifyObservers(tile);
    }

    /**
     * Tells whether the coordinates of the tile are valid or not
     *
     * @param tile the tile
     * @return are the coordinates within the dimensions of the board?
     */
    public boolean isValid(PlaceTile tile) {
        return placeBoard.isValid(tile);
    }

    /**
     * Return a string representation of the board.  It displays the tile color as
     * a single character hex value in the range 0-F.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return placeBoard.toString();
    }
}
