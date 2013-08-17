package de.skuzzle.polly.sdk.httpv2;

import java.util.List;


public interface HttpManagerV2 {

    public void addMenuEntry(MenuEntry me);
    
    public List<MenuEntry> getMenuEntries();
}
