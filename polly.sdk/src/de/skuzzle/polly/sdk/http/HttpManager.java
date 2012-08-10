package de.skuzzle.polly.sdk.http;

import java.io.File;
import java.io.IOException;




public interface HttpManager {
    
    public final static String HTTP_ADMIN_PERMISSION = "polly.permission.HTTP_ADMIN";
    
    public abstract void startWebServer() throws IOException;
    
    public abstract void stopWebServer();
    
    public abstract boolean isRunning();
    
    public abstract void removeMenuUrl(String category, String name);
    
    public abstract void addMenuUrl(String category, String name);

    public abstract void addHttpAction(HttpAction action);
    
    public abstract void addHttpEventListener(HttpEventListener listener);
    
    public abstract void removeHttpEventListener(HttpEventListener listener);

    public abstract HttpTemplateContext errorTemplate(String errorHeading,
        String errorDescription, HttpSession session);

    public abstract File getPage(String name);

    String getPublicHost();

    public abstract int getPort();

    public abstract boolean validateSessions(HttpSession session);

    public abstract HttpSession findSession(String id);

}