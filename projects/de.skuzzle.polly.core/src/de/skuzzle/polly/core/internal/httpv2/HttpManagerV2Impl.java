package de.skuzzle.polly.core.internal.httpv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.sdk.httpv2.HttpHook;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.MenuCategory;
import de.skuzzle.polly.sdk.httpv2.MenuEntry;


public class HttpManagerV2Impl implements WebinterfaceManager {

    private final Map<String, MenuCategory> categories;
    private final HttpServletServer server;
    private final List<MenuEntry> topMenu;
    
    public HttpManagerV2Impl(HttpServletServer server) {
        this.server = server;
        this.topMenu = new ArrayList<>();
        this.categories = new HashMap<String, MenuCategory>();
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
        if (!this.categories.containsKey(category)) {
            this.categories.put(category, new MenuCategory(category));
        }
        this.categories.get(category).getContent().add(me);
    }



    @Override
    public List<MenuCategory> getMenuCategories() {
        return new ArrayList<>(this.categories.values());
    }



    @Override
    public void addLoginHook(HttpHook hook) {
    }



    @Override
    public void addLogoutHook(HttpHook hook) {
    }
    
}
