package de.skuzzle.polly.sdk.http;

import java.io.File;
import java.io.IOException;

import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;




public interface HttpManager {
    
    public final static String HTTP_ADMIN_PERMISSION = "polly.permission.HTTP_ADMIN";
    public final static String FILE_REQUEST_PREFIX = "/file:";
    public static final String MEMORY_REQUEST_PREFIX = "/memory:";
    
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

    public abstract void closeSession(HttpSession session);

    public abstract HttpSession findSession(String id);

    public abstract void cleanUpSessions();

    public abstract HttpTemplateContext executeAction(HttpEvent e) 
            throws HttpTemplateException, InsufficientRightsException;

    String escapeHtml(String s);

    String makeActionLink(String actionName, User user, String prefix,
        String postfix);

    public File getTemplateRoot();

    public void putMemoryFile(String name, byte[] file);

}