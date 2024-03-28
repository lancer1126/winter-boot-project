package fun.lance.boot;

public class WinterApplicationShutdownHook implements Runnable {

    private volatile boolean shutdownHookAdditionEnabled = false;

    void enableShutdownHookAddition() {
        this.shutdownHookAdditionEnabled = true;
    }

    @Override
    public void run() {

    }
}
