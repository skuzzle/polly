package polly.network.protocol;


public interface Constants {
    
    public final static String LOG_LIST = "LOG_LIST";
    public final static String USER_NAME = "USER_NAME";
    public final static String PASSWORD = "PASSWORD";
    
    
    
    public static enum RequestType {
        LOGIN, LOGOUT, LIVE_LOG_ON, LIVE_LOG_OFF, UPDATE, IRC_FORWARD_ON, IRC_FORWARD_OFF, 
        UPDATE_CACHE;
    }
    
    
    
    public static enum ResponseType {
        ERROR, LOGGED_IN, ACCEPTED, LOG_ITEM, FILE, IGNORED, LIVE_LOG_ON, LIVE_LOG_OFF, 
        LOGOUT, IRC_FORWARD_ON, IRC_FORWARD_OFF, UPDATE_DONE;
    }
    
    
    
    public static enum ErrorType {
        UNKNOWN_USER, INVALID_PASSWORD, LIMIT_EXCEEDED, BAD_REQUESTS, 
        INSUFFICIENT_RIGHTS, REQUEST_IGNORED, LOGIN_TIMEOUT;
    }

}
