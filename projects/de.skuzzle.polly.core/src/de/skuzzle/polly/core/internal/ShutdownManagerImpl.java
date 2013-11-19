package de.skuzzle.polly.core.internal;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;



import de.skuzzle.polly.core.Polly;
import de.skuzzle.polly.process.JavaProcessExecutor;
import de.skuzzle.polly.process.ProcessExecutor;
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

    
    /** 
     * Sun property pointing the main class and its arguments. 
     * Might not be defined on non Hotspot VM implementations.
     */
    public static final String SUN_JAVA_COMMAND = "sun.java.command"; //$NON-NLS-1$
    
    
    private AtomicBoolean isSafeShutdown;

    
    private CompositeDisposable shutdownList;
    
    public ShutdownManagerImpl() {
        this.isSafeShutdown = new AtomicBoolean(false);
        this.shutdownList = new CompositeDisposable();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!ShutdownManagerImpl.this.isSafeShutdown.get()) {
                    logger.warn("Unexpected shutdown. Trying to shutdown all " + //$NON-NLS-1$
                    		"resources properly."); //$NON-NLS-1$
                    ShutdownManagerImpl.this.emergencyShutdown();
                }
            }
        });
    }
    
    
    
    public CompositeDisposable getShutdownList() {
        return this.shutdownList;
    }
    
    
    
    public synchronized void addDisposable(Disposable d) {
        this.shutdownList.addLast(d);
    }
    
    
    
    @Override
    public void restart() {
        this.restart(Polly.getCommandLine());
    }
    
    
    /*
     * Implementation is adapted from 
     * http://java.dzone.com/articles/programmatically-restart-java
     */
    
    @Override
    public synchronized void restart(String commandLine) {
        // properly shutdown all loaded components
        this.shutdown(false);
        logger.info("Preparing to restart..."); //$NON-NLS-1$
        logger.debug("Commandline arguments: " + commandLine); //$NON-NLS-1$
        
        try {
            final ProcessExecutor pe = JavaProcessExecutor.getCurrentInstance(false);

            pe.addCommandsFromString("-jar polly.jar -returninfo \"Returned from restart\""); //$NON-NLS-1$

            if (commandLine != null && !commandLine.equals("")) { //$NON-NLS-1$
                pe.addCommandsFromString(commandLine);
            }
            
            logger.trace("Java command: '" + pe.toString() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            // execute the command in a shutdown hook, to be sure that all the
            // resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        pe.start();
                        logger.trace("Executed: " + pe.toString()); //$NON-NLS-1$
                        logger.info("New polly instance initialized."); //$NON-NLS-1$
                    } catch (IOException e) {
                        logger.error("Could not start new polly instance.", e); //$NON-NLS-1$
                    }
                }
            });
            
        } catch (Exception e) {
            logger.error("Could not start new polly instance", e); //$NON-NLS-1$
        } finally {
            // All resources have been unloaded, so perform shutdown
            logger.warn("Exiting current polly process."); //$NON-NLS-1$
            System.exit(0);
        }
    }
    
    
    
    public synchronized void shutdown(boolean exit) {
        this.isSafeShutdown.set(true);
        logger.info("Shutting down all components."); //$NON-NLS-1$
        try {
            this.shutdownList.dispose();
        } catch (DisposingException e) {
            logger.error("One or more components failed to properly unload. " + //$NON-NLS-1$
            		"Last exception trace:", e); //$NON-NLS-1$
        }
        
        logger.trace("Remaining active threads: " + Thread.activeCount()); //$NON-NLS-1$
        logger.trace("Remaining stacktraces:"); //$NON-NLS-1$
        this.printRemainingThreads();
        
        if (exit) {
            logger.info("All connections closed. Now exiting the whole program. ByeBye"); //$NON-NLS-1$
            LogManager.shutdown();
            System.exit(0);
        }
    }
    
    
    
    public void shutdown() {
        this.shutdown(true);
    }
    
    
    
    private void emergencyShutdown() {
        try {
            LogManager.shutdown();
            this.shutdownList.dispose();
        } catch (DisposingException e) {
            e.printStackTrace();
        }
    }
    
    
    
    private void printRemainingThreads() {
        Thread[] active = new Thread[Thread.activeCount()];
        Thread.enumerate(active);
        for (Thread t : active) {
            logger.trace("Thread: " + t.toString()); //$NON-NLS-1$
            for (StackTraceElement e : t.getStackTrace()) {
                logger.trace("    " + e.toString()); //$NON-NLS-1$
            }
        }
    }
}