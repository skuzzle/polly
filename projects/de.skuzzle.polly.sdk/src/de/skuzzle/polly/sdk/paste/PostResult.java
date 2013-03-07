package de.skuzzle.polly.sdk.paste;


/**
 * This class represents the Result of a post request. It contains the response
 * URL aswell as the source of the response page.
 * 
 * @author Simon
 * @since 0.7
 */
public class PostResult {

    private String resultURL;
    private String resultString;
    
    

    public PostResult(String resultURL, String resultString) {
        super();
        this.resultURL = resultURL;
        this.resultString = resultString;
    }
    
    
    
    /**
     * Gets the source of the response page.
     * 
     * @return The response source.
     */
    public String getResultString() {
        return resultString;
    }
    
    
    
    /**
     * Gets the response URL.
     * 
     * @return The response URL.
     */
    public String getResultURL() {
        return resultURL;
    }
}