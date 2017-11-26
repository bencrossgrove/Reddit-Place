package place;

public class Logger {

    private static boolean DEBUG = true;

    public static void log(String message){
        if (DEBUG){
            System.out.println(message);
        }
    }

}
