package de.skuzzle.polly.sdk.http;


public interface HttpAction {
    
    public abstract String getName();

    public abstract void execute(HttpEvent e, HttpTemplateContext context);
}
