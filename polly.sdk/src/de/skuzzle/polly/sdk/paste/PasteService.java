package de.skuzzle.polly.sdk.paste;

import java.util.Date;


/**
 * This interface grants access to internet paste services. It is preimplemented
 * in {@link AbstractPasteService}.
 * 
 * @author Simon
 * @since 0.7
 */
public interface PasteService {

    /**
     * Performs the paste job. That is, sends the string message to your paste
     * provider and returns the URL under which this paste will be viewable.
     *  
     * @param message The message to paste.
     * @return The URL to the paste result.
     * @throws Exception If an error occurred during pasting.
     */
    public abstract String paste(String message) throws Exception;
    
    
    
    /**
     * Gets the name of this PasteService.
     * 
     * @return The service name.
     */
    public abstract String getName();
    
    
    
    /**
     * Gets the time of the last invocation of {@link #paste(String)}.
     * 
     * @return The last paste time.
     */
    public abstract Date getLastPasteTime();
}