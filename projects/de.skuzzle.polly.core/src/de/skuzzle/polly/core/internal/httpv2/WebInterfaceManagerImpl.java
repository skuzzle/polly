package de.skuzzle.polly.core.internal.httpv2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.sdk.httpv2.HttpHook;
import de.skuzzle.polly.sdk.httpv2.MenuCategory;
import de.skuzzle.polly.sdk.httpv2.MenuEntry;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;

public class WebInterfaceManagerImpl implements WebinterfaceManager {

    private final Map<String, MenuCategory> categories;
    private final HttpServletServer server;
    private final List<MenuEntry> topMenu;
    private final String webRoot;
    private final String publicHost;
    private final int port;
    private final boolean ssl;


    public WebInterfaceManagerImpl(HttpServletServer server, String webRoot, 
            String publicHost, int port, boolean ssl) {
        this.port = port;
        this.ssl = ssl;
        this.webRoot = webRoot;
        this.publicHost = publicHost;
        this.server = server;
        this.topMenu = new ArrayList<>();
        this.categories = new HashMap<String, MenuCategory>();
    }



    @Override
    public String getWebRoot() {
        return this.webRoot;
    }



    @Override
    public void addTopMenuEntry(MenuEntry me) {
        this.topMenu.add(me);
    }



    @Override
    public List<MenuEntry> getTopMenuEntries() {
        return this.topMenu;
    }



    @Override
    public HttpServletServer getServer() {
        return this.server;
    }



    @Override
    public void addCategory(MenuCategory me) {
        this.categories.put(me.getName(), me);
    }



    @Override
    public void addMenuEntry(String category, MenuEntry me) {
        this.categories.get(category).getContent().add(me);
    }



    @Override
    public List<MenuCategory> getMenuCategories() {
        final List<MenuCategory> result = new ArrayList<>(this.categories.values());
        Collections.sort(result);
        return result;
    }



    @Override
    public void addLoginHook(HttpHook hook) {
    }



    @Override
    public void addLogoutHook(HttpHook hook) {
    }



    @Override
    public String getPublicHost() {
        return this.publicHost;
    }
    
    
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    
    @Override
    public boolean isSSL() {
        return this.ssl;
    }
}
