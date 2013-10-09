package de.skuzzle.polly.sdk.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class PollyBundle {
    
    private final ResourceBundle wrapped;
    
    
    public PollyBundle(ResourceBundle wrapped) {
        this.wrapped = wrapped;
    }

    
    
    public String get(String key) {
        while (true) {
            try {
                final String newKey = this.wrapped.getString(key);
                if (key.equals(newKey)) {
                    return key;
                }
                key = newKey;
            } catch (MissingResourceException e) {
                return key;
            }
        }
    }
}
