package de.skuzzle.polly.core.internal.httpv2;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.sdk.httpv2.HttpManagerV2;
import de.skuzzle.polly.sdk.httpv2.MenuEntry;


public class HttpManagerV2Impl implements HttpManagerV2 {

    private final List<MenuEntry> entries;
    
    
    public HttpManagerV2Impl() {
        this.entries = new ArrayList<>();
    }
    
    
    
    @Override
    public void addMenuEntry(MenuEntry me) {
        this.entries.add(me);
    }

    
    
    
    @Override
    public List<MenuEntry> getMenuEntries() {
        return this.entries;
    }
}
