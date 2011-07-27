package polly.core;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.CompositeDisposable;
import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.ShutdownManager;
import de.skuzzle.polly.sdk.exceptions.DisposingException;



/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 * @since Beta 0.5
 */
public class ShutdownManagerImpl implements ShutdownManager {

    private static Logger logger = Logger.getLogger(ShutdownManagerImpl.class.getName());

    private static ShutdownManagerImpl instance;
    
    public static synchronized ShutdownManagerImpl get() {
        if (instance == null) {
            instance = new ShutdownManagerImpl();
        }
        return instance;
    }
    
    
    
    private CompositeDisposable shutdownList;
    
    private ShutdownManagerImpl() {
        this.shutdownList = new CompositeDisposable();
    }
    
    
    
    public CompositeDisposable getShutdownList() {
        return this.shutdownList;
    }
    
    
    
    public synchronized void addDisposable(Disposable d) {
        this.shutdownList.addLast(d);
    }
    
    
    
    @Override
    public void restart() {
        this.restart("");
    }
    
    
    
    @Override
    public void restart(String args) {
        throw new UnsupportedOperationException();
    }
    
    
    
    public void shutdown(boolean exit) {
        logger.info("Shutting down all components.");
        try {
            this.shutdownList.dispose();
        } catch (DisposingException e) {
            logger.error("One ore more components failed to properly unload. " +
            		"Last exception trace:", e);
        }
        
        logger.trace("Remaining active threads: " + Thread.activeCount());
        logger.trace("Remaining stacktraces:");
        this.printRemainingThreads();
        
        if (exit) {
            logger.info("All connections closed. Now exiting the whole program. ByeBye");
            System.exit(0);
        }
    }
    
    
    
    public void shutdown() {
        this.shutdown(true);
    }
    
    
    
    private void printRemainingThreads() {
        Thread[] active = new Thread[Thread.activeCount()];
        Thread.enumerate(active);
        for (Thread t : active) {
            logger.trace("Thread: " + t.toString());
            for (StackTraceElement e : t.getStackTrace()) {
                logger.trace("    " + e.toString());
            }
        }
    }
}