package de.skuzzle.polly.process;


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
    
    
    
    public StreamHandler getInputHandler() {
        return this.inputHandler;
    }
    
    
    
    
    public StreamHandler getErrorHandler() {
        return this.errorHandler;
    }
}