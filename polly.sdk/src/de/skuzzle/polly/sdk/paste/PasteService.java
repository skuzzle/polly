package de.skuzzle.polly.sdk.paste;

import java.util.Date;


/**
 * TODO: comment
 * @author Simon
 * @since 0.7
 */
public interface PasteService {

    public abstract String paste(String message) throws Exception;
    
    public abstract String getName();
    
    public abstract Date getLastPasteTime();
}