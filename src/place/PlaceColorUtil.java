package place;

/**
 * Used to retrieve a color based on it's color code
 * @author Ben Crossgrove
 */

public class PlaceColorUtil {

    /**
     * retrieve the corresponding color from PlaceColor enum
     *
     * @param colorCode code of desired color
     * @return the color
     */
    public static PlaceColor getColor(int colorCode) {
        for (PlaceColor color : PlaceColor.values())
            if (color.getNumber() == colorCode) {
                return color;
            }
        return PlaceColor.WHITE;
    }

}
