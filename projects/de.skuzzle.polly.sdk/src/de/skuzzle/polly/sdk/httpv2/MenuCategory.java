package de.skuzzle.polly.sdk.httpv2;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class MenuCategory implements Comparable<MenuCategory> {

    private final int sortId;
    private final String name;
    private final Set<MenuEntry> content;
    
    
    public MenuCategory(int sortId, String name) {
        this.sortId = sortId;
        this.name = name;
        this.content = new TreeSet<>();
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public Collection<MenuEntry> getContent() {
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

    @Override
    public int hashCode() {
        return Math.abs(super.hashCode());
    }

    @Override
    public int compareTo(MenuCategory o) {
        return this.sortId - o.sortId;
    }
}
