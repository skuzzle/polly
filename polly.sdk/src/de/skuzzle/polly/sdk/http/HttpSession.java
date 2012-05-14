package de.skuzzle.polly.sdk.http;

import java.net.InetSocketAddress;

import de.skuzzle.polly.sdk.model.User;



public class HttpSession {
    
    private String id;
    private long started;
    private long lastAction;
    private InetSocketAddress remoteIp;
    private User user;
    
    
    
    public HttpSession(String id, InetSocketAddress remoteIp) {
        this.id = id;
        this.remoteIp = remoteIp;
        this.started = System.currentTimeMillis();
        this.lastAction = System.currentTimeMillis();
    }
    
    
    
    public synchronized User getUser() {
        return this.user;
    }
    
    
    
    public synchronized void setUser(User user) {
        this.user = user;
    }
    
    
    
    public synchronized long getLastAction() {
        return this.lastAction;
    }
    
    
    
    public synchronized void setLastAction(long lastAction) {
        this.lastAction = lastAction;
    }
    
    
    
    public String getId() {
        return this.id;
    }
    
    
    
    public long getStarted() {
        return this.started;
    }
    
    
    
    public InetSocketAddress getRemoteIp() {
        return this.remoteIp;
    }
    
    
    
    @Override
    public String toString() {
        return "HttpSession [ip = " + this.remoteIp + ", id = " + this.id + "]";
    }
}