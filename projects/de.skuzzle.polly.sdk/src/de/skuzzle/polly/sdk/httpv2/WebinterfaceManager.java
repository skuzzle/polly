package de.skuzzle.polly.sdk.httpv2;

import java.util.List;

import de.skuzzle.polly.http.api.HttpServletServer;


public interface WebinterfaceManager {
    
    public final static String USER = "user"; //$NON-NLS-1$
    
    public final static String ADD_MENU_ENTRY = "ADD_MENU_ENTRY"; //$NON-NLS-1$
    
    public void addTopMenuEntry(MenuEntry me);
    
    public List<MenuEntry> getTopMenuEntries();

    public void addCategory(MenuCategory me);
    
    public void addMenuEntry(String category, MenuEntry me);
    
    public List<MenuCategory> getMenuCategories();

    public HttpServletServer getServer();
    
    public void addLoginHook(HttpHook hook);
    
    public void addLogoutHook(HttpHook hook);
    
    public String getWebRoot();

    public String getPublicHost();
    
    /**
     * Gets the port on which the webinterface server is listening
     * @return The port
     */
    public int getPort();

    public boolean isSSL();
}
