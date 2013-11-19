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
    
    
    
    public String get(String key, String obj1, String...obj2) {
        final Object[] objs = new Object[obj2.length + 1];
        objs[0] = obj1;
        System.arraycopy(obj2, 0, objs, 1, obj2.length);
        return String.format(this.get(key), objs);
    }
}
