package de.skuzzle.polly.core.internal.irc;

import de.skuzzle.polly.sdk.Disposable;


public interface MessageScheduler extends Runnable, Disposable {

    public void addMessage(String channel, String message, Object source);
    
    public void start();
    
    public void setMessageDelay(int delay);
    
    
}