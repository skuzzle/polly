package de.skuzzle.polly.sdk.httpv2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.skuzzle.polly.sdk.roles.SecurityObject;

public class MenuEntry implements SecurityObject {

    private final String name;
    private final String url;
    private final String description;
    private final Set<String> permissions;


    public MenuEntry(String name, String url, String description, String...permissions) {
        super();
        this.name = name;
        this.url = url;
        this.description = description;
        this.permissions = new HashSet<String>(Arrays.asList(permissions));
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
}
