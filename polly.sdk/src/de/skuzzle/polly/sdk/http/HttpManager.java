package de.skuzzle.polly.sdk.http;




public interface HttpManager {
    
    public final static String HTTP_ADMIN_PERMISSION = "polly.permission.HTTP_ADMIN";
    
    public abstract void startWebServer();
    
    public abstract void stopWebServer();
    
    public abstract boolean isRunning();
    
    public abstract void addMenuUrl(String name);
    
    public abstract void removeMenuUrl(String name);
    
    public abstract void addHttpAction(HttpAction action);
    
    public abstract void addHttpEventListener(HttpEventListener listener);
    
    public abstract void removeHttpEventListener(HttpEventListener listener);

    HttpTemplateContext errorTemplate(String errorHeading,
        String errorDescription, HttpSession session);
}