package de.skuzzle.polly.sdk.resources;

import java.util.Locale;
import java.util.ResourceBundle;

import sun.reflect.Reflection;


public class Resources {

    /** This field will be set by polly during intialization */
    static Locale pollyLocale;
    
    
    
    public static PollyBundle get(String family) {
        final Class<?> caller = Reflection.getCallerClass();
        final ClassLoader cl = caller.getClassLoader();
        final ResourceBundle r = ResourceBundle.getBundle(family, pollyLocale, cl);
        return new PollyBundle(r);
    }
    
    
    
    public static Locale getLocale() {
        return pollyLocale;
    }
}
