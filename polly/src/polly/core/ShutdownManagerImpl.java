package polly.core;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

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

    
    /** 
     * Sun property pointing the main class and its arguments. 
     * Might not be defined on non Hotspot VM implementations.
     */
    public static final String SUN_JAVA_COMMAND = "sun.java.command";
    
    
    
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
    
    
    /*
     * Implementation is adapted from 
     * http://java.dzone.com/articles/programmatically-restart-java
     */
    
    @Override
    public void restart(String commandLine) {
        this.shutdown(false);
        try {
            String s = System.getProperty("file.separator");
            // java binary
            String java = System.getProperty("java.home") + s +"bin" + s + "java";
            // vm arguments
            List<String> vmArguments = 
                ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuffer vmArgsOneLine = new StringBuffer();
            for (String arg : vmArguments) {
                // if it's the agent argument : we ignore it otherwise the
                // address of the old application and the new one will be in conflict
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
            // init the command to execute, add the vm args
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + 
                vmArgsOneLine);

            // program main and program arguments
            String command = System.getProperty(SUN_JAVA_COMMAND);
            System.out.println("Command: " + command);
            String[] mainCommand = command == null ? new String[]{""} : command.split(" ");
            // program main is a jar
            //if (mainCommand[0].endsWith(".jar")) {
                // if it's a jar, add -jar mainJar
                cmd.append("-jar polly.jar");// + new File(mainCommand[0]).getPath());
            //} else {
                // else it's a .class, add the classpath and mainClass
            //    cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + 
            //        mainCommand[0]);
            //}
            // finally add program arguments
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }
            // execute the command in a shutdown hook, to be sure that all the
            // resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        logger.info("Starting new polly instance: " + cmd);
                        Runtime.getRuntime().exec(cmd.toString());
                        logger.info("New polly instance initialized.");
                    } catch (IOException e) {
                        logger.error("Could not start new polly instance.");
                    }
                }
            });
            
            this.shutdown(true);
        } catch (Exception e) {
            logger.error("Could not start new polly instance", e);
        }
        System.exit(0);
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