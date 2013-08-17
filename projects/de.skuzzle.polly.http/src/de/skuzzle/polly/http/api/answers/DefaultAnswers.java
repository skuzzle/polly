package de.skuzzle.polly.http.api.answers;

/**
 * Contains some modifiable default {@link HttpAnswer HttpAnswers} used by the http
 * server implementation.
 * 
 * @author Simon Taddiken
 */
public class DefaultAnswers {

    /**
     * Answer if no {@link de.skuzzle.polly.http.api.HttpEventHandler HttpEventHandler}
     * could handle the request. 
     */
    public static HttpAnswer FILE_NOT_FOUND = 
        HttpAnswers.createStringAnswer("File not found");
    
    /** Answer if blocked session performed a request */
    public static HttpAnswer SESSION_BLOCKED = 
        HttpAnswers.createStringAnswer("Your session is blocked");
}
