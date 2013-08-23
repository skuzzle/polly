package de.skuzzle.polly.sdk.httpv2;

import java.util.List;

import de.skuzzle.polly.http.api.HttpServletServer;


public interface HttpManagerV2 {

    public final static String HTTP_ADMIN_PERMISSION = "polly.permission.HTTP_ADMIN";

    public void addMenuEntry(MenuEntry me);
    
    public List<MenuEntry> getMenuEntries();

    public HttpServletServer getServer();
    
}
