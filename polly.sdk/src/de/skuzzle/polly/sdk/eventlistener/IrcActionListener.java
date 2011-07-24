package de.skuzzle.polly.sdk.eventlistener;


public interface IrcActionListener {

    public abstract void userSpotted(ChannelEvent e);
    
    public abstract void userLost(IrcEvent e);
}