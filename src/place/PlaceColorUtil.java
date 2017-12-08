package place;

import java.awt.*;

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

    public static float[] getHSB(PlaceColor color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        float[] hsv = new float[3];
        return Color.RGBtoHSB(red, green, blue ,hsv);
    }

}
