package polly.core;


public interface ModuleStates {

    public static final int PLUGINS_READY = 1;
    
    public static final int PERSISTENCE_READY = 2;
    
    public static final int IRC_READY = 3;
    
    public static final int TELNET_READY = 4;
    
    public static final int USERS_READY = 5;
    
    public static final int PLUGINS_NOTIFIED = 6;
}