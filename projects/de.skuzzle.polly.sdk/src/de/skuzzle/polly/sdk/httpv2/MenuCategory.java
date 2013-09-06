package de.skuzzle.polly.sdk.httpv2;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class MenuCategory {

    private final String name;
    private final List<MenuEntry> content;
    
    
    public MenuCategory(String name) {
        this.name = name;
        this.content = new ArrayList<>();
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public List<MenuEntry> getContent() {
        return this.content;
    }
    
    
    
    public MenuCategory addEntry(MenuEntry me) {
        this.content.add(me);
        return this;
    }
    
    
    
    public boolean mustRender(User user, RoleManager roleManager) {
        for (final MenuEntry me : this.getContent()) {
            if (roleManager.canAccess(user, me)) {
                return true;
            }
        }
        return false;
    }
}
