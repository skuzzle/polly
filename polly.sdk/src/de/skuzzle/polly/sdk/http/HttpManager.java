package de.skuzzle.polly.sdk.http;




public interface HttpManager {
    
    public abstract void startWebServer();
    
    public abstract void stopWebServer();
    
    public abstract boolean isRunning();
    
    public abstract void addMenuUrl(String name);
    
    public abstract void removeMenuUrl(String name);
    
    public abstract void addHttpAction(HttpAction action);
    
    public abstract void addHttpEventListener(HttpEventListener listener);
    
    public abstract void removeHttpEventListener(HttpEventListener listener);
}