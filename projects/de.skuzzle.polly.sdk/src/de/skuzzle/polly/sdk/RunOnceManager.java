package de.skuzzle.polly.sdk;

/**
 * This class provides the capabilities to run some code only once in polly's live time.
 * Once an action has been run by this class, it is remembered and not run again, even
 * if polly restarts.
 * 
 * @author Simon Taddiken
 */
public interface RunOnceManager {

    /**
     * Registers an action which will only be run once. Once the action has been executed,
     * its class name is stored within a configuration file and will not be run again.
     * @param r The action to execute only once.
     */
    public void registerAction(Runnable r);
}