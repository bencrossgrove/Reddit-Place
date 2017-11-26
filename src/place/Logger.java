package place;

/**
 * Used for debugging or displaying messages
 * @author Ben Crossgrove
 */

public class Logger {

    // set to true to enable Logger.debug messages
    private static boolean DEBUG = false;

    public static void debug(String message){
        if (DEBUG){
            System.out.println(message);
        }
    }

    public static void log(String message){
        System.out.println(message);
    }

}
