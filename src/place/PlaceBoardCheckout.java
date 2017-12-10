package place;

/**
 * Based on:
 * http://tutorials.jenkov.com/java-concurrency/thread-signaling.html
 *
 * @author Ben Crossgrove
 */

public class PlaceBoardCheckout {

    private final PlaceBoardObservable board;
    private boolean wasSignalled = false;

    public PlaceBoardCheckout(PlaceBoardObservable model) {
        this.board = model;
    }

    public void doWait() {
        synchronized (board) {
            if(!wasSignalled){
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

    public void doNotify() {
        synchronized (board) {
            Logger.debug("notified " + Thread.currentThread().getId());
            wasSignalled = true;
            board.notify();
        }
    }
}
