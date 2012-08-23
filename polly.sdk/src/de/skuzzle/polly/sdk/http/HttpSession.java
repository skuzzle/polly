package de.skuzzle.polly.sdk.http;

import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;

import de.skuzzle.polly.sdk.model.User;



public class HttpSession {
    
    private String id;
    private long started;
    private long lastAction;
    private InetAddress remoteIp;
    private User user;
    private String lastUri;
    private Map<String, Object> data;
    
    
    
    public HttpSession(String id, InetAddress remoteIp) {
        this.id = id;
        this.remoteIp = remoteIp;
        this.started = System.currentTimeMillis();
        this.lastAction = System.currentTimeMillis();
        this.lastUri = "";
        this.data = new TreeMap<String, Object>();
    }
    
    
    
    public boolean isTimedOut(int timeOut) {
        return System.currentTimeMillis() - this.lastAction > timeOut;
    }
    
    
    
    public synchronized User getUser() {
        return this.user;
    }
    
    
    
    public synchronized void setUser(User user) {
        this.user = user;
    }
    
    
    
    public boolean isLoggedIn() {
        return this.user != null;
    }
    
    
    
    public synchronized long getLastAction() {
        return this.lastAction;
    }
    
    
    
    public synchronized void setLastAction(long lastAction) {
        this.lastAction = lastAction;
    }
    
    
    
    public synchronized String getLastUri() {
        return this.lastUri;
    }
    
    
    
    public synchronized void setLastUri(String lastUri) {
        this.lastUri = lastUri;
    }
    
    
    
    public String getId() {
        return this.id;
    }
    
    
    
    public long getStarted() {
        return this.started;
    }
    
    
    
    public InetAddress getRemoteIp() {
        return this.remoteIp;
    }
    
    
    
    public void putDtata(String key, Object o) {
        this.data.put(key, o);
    }
    
    
    
    public Object get(String key) {
        return this.data.get(key);
    }
    
    
    
    public void removeData(String key) {
        this.data.remove(key);
    }
    
    
    
    @Override
    public String toString() {
        return "HttpSession [ip = " + this.remoteIp + ", id = " + this.id + "]";
    }
}