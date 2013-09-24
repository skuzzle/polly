package de.skuzzle.polly.sdk.httpv2.html;


public interface Acceptor {
    
    public Object parseFilter(String filter);
    
    public boolean accept(Object filter, Object cellValue);
}
