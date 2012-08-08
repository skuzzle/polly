package de.skuzzle.polly.sdk.http;

public class HttpTemplateException extends Exception {

    private static final long serialVersionUID = 1L;

    private String heading;
    private HttpSession session;
    

    
    public HttpTemplateException(String heading, String message, 
            HttpSession session) {
        super(message);
        this.heading = heading;
        this.session = session;
    }
    
    
    
    public HttpTemplateException(HttpSession session, Throwable cause) {
        super(cause.getMessage(), cause);
        this.heading = "Unexpected execution error";
        this.session = session;
    }
    
    
    
    public HttpSession getSession() {
        return this.session;
    }
    
    
    
    public String getHeading() {
        return this.heading;
    }
}
