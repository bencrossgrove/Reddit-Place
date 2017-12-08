package place;

import java.awt.*;

/**
 * Used to retrieve a color based on it's color code
 *
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

    /**
     * return the hue, saturation and brightness values of a given PlaceColor
     *
     * @param color the color to retrieve HSB values from
     * @return array of HSB values
     */
    public static float[] getHSB(PlaceColor color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        float[] hsv = new float[3];
        return Color.RGBtoHSB(red, green, blue, hsv);
    }

}
