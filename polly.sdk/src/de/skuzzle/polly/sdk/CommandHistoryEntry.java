package de.skuzzle.polly.sdk;

/**
 * Represents a history entry for a command. The {@link CommandManager} has a history
 * of the last command executed on each channel which can be retrieved using 
 * {@link CommandManager#getLastCommand(String)}
 * 
 * @author Simon
 *
 */
public interface CommandHistoryEntry {

    /**
     * Gets the executed command.
     * 
     * @return The command.
     */
    public abstract Command getCommand();
    
    
    
    /**
     * Gets the signature with which the command has been executed.
     * 
     * @return The signature.
     */
    public abstract Signature getSignature();
}
