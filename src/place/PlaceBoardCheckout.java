package place;

/**
 * Based on:
 * http://tutorials.jenkov.com/java-concurrency/thread-signaling.html
 * Used to give client time to setup / prevent tile changes from coming in in rapid fashion
 *
 * @author Ben Crossgrove
 */

public class PlaceBoardCheckout {

    private final PlaceBoardObservable board;
    private boolean wasSignalled = false;

    public PlaceBoardCheckout(PlaceBoardObservable model) {
        this.board = model;
    }

    /**
     * wait when tile changes made / new user login
     */
    public void doWait() {
        synchronized (board) {
            while (!wasSignalled) {
                try {
                    Logger.debug("before waiting " + Thread.currentThread().getId());
                    board.wait();
                    Logger.debug("after waiting ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            wasSignalled = false;
        }
    }

    /**
     * notify used when ok to proceed
     */
    public void doNotify() {
        synchronized (board) {
            Logger.debug("notified " + Thread.currentThread().getId());
            wasSignalled = true;
            board.notify();
        }
    }
}
