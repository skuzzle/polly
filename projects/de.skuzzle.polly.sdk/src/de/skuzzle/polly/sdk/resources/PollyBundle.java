package de.skuzzle.polly.sdk.resources;

import java.util.ResourceBundle;


public class PollyBundle {
    
    private final ResourceBundle wrapped;
    
    
    public PollyBundle(ResourceBundle wrapped) {
        this.wrapped = wrapped;
    }

    
    
    public String get(String key, Object...format) {
        final String s = this.wrapped.getString(key);
        return s;
    }
}
