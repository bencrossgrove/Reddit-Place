package place;

/**
 * Used for debugging or displaying messages
 * @author Ben Crossgrove
 */

public class Logger {

    private static boolean DEBUG = true;

    public static void debug(String message){
        if (DEBUG){
            System.out.println(message);
        }
    }

    public static void log(String message){
        System.out.println(message);
    }

}
