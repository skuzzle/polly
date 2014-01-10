package de.skuzzle.polly.sdk.httpv2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.roles.SecurityObject;

public class MenuEntry implements SecurityObject, Comparable<MenuEntry> {

    private final String name;
    private final String url;
    private final String description;
    private final Set<String> permissions;
    private final List<MenuEntry> subEntries;



    public MenuEntry(String name, String url, String description, String... permissions) {
        super();
        this.name = name;
        this.url = url;
        this.description = description;
        this.permissions = new HashSet<String>(Arrays.asList(permissions));
        this.subEntries = new ArrayList<>();
    }



    public void addSubEntry(MenuEntry e) {
        this.subEntries.add(e);
    }



    public boolean mustRenderSubEntries(User user, RoleManager roles) {
        for (final MenuEntry e : this.subEntries) {
            if (roles.canAccess(user, e)) {
                return true;
            }
        }
        return false;
    }



    public Collection<MenuEntry> getSubEntries() {
        return this.subEntries;
    }



    public String getName() {
        return this.name;
    }



    public String getUrl() {
        return this.url;
    }



    public String getDescription() {
        return this.description;
    }



    @Override
    public Set<String> getRequiredPermission() {
        return this.permissions;
    }



    @Override
    public int hashCode() {
        return Math.abs(super.hashCode());
    }



    @Override
    public int compareTo(MenuEntry other) {
        return this.name.compareTo(other.name);
    }
}
