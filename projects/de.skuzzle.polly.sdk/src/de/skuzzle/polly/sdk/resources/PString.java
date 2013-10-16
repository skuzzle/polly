package de.skuzzle.polly.sdk.resources;


public final class PString {

    public final String s;
    
    
    public PString(String s) {
        this.s = s;
    }
    
    
    
    public String s(Object...args) {
        return String.format(this.s, args);
    }
    
    
    
    @Override
    public String toString() {
        return this.s;
    }
}
