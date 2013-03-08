package de.skuzzle.polly.process;

/**
 * Wrapper class that contains the started process along with its 
 * {@link StreamHandler StreamHandlers} and {@link ProcessWatcher}.
 * 
 * @author Simon Taddiken
 */
public class ProcessWrapper {

    private Process process;
    private StreamHandler inputHandler;
    private StreamHandler errorHandler;
    
    
    ProcessWrapper(Process process, StreamHandler inputHandler, 
            StreamHandler errorHandler) {
        this.process = process;
        this.inputHandler = inputHandler;
        this.errorHandler = errorHandler;
    }
    
    
    
    /**
     * Gets the wrapped process.
     * 
     * @return The process.
     */
    public Process getProcess() {
        return this.process;
    }
    
    
    
    /**
     * Gets the {@link StreamHandler} that reads the standard output of this process.
     * @return The StreamHandler.
     */
    public StreamHandler getInputHandler() {
        return this.inputHandler;
    }
    
    
    
    /**
     * Gets the {@link StreamHandler} that reads the standard error output of this 
     * process.
     * @return The StreamHandler.
     */
    public StreamHandler getErrorHandler() {
        return this.errorHandler;
    }
}