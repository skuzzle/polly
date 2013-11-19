package de.skuzzle.polly.sdk.resources;


public abstract class Constants {
    
    public final static String bind(String message, Object...format) {
        return String.format(message, format);
    }

}
