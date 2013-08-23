/*
 * Copyright 2013 Simon Taddiken
 *
 * This file is part of Polly HTTP API.
 *
 * Polly HTTP API is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 *
 * Polly HTTP API is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with Polly HTTP API. If not, see http://www.gnu.org/licenses/.
 */
package de.skuzzle.polly.http.internal;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.TrafficInformation;


class HttpSessionImpl implements HttpSession {
    
    private final static int EVENTS_TO_BUFFER = 10;

    private final transient HttpServerImpl server;
    private final String id;
    private final long timestamp;
    private final Map<String, Object> attached;
    private final TrafficInformation trafficInfo;
    private final Queue<HttpEvent> events;
    private Date lastAction;
    private boolean doKill;
    
    /** Timestamp at which blocking the session started */
    private long blockStamp;
    
    /** Time in ms how long this session should be blocked */
    private long blockTime;
    
    
    private Date expirationDate;
    private boolean pending;
    

    
    
    public HttpSessionImpl(HttpServerImpl server, String id) {
        
        this.server = server;
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.attached = new HashMap<>();
        this.trafficInfo = new TrafficInformationImpl();
        this.events = new ArrayDeque<>();
        // make sure session is initially unblocked
        this.blockStamp = Long.MAX_VALUE;
        this.blockTime = 0;
    }
    
    
    
    
    public void setPending(boolean pending) {
        this.pending = pending;
    }
    
    
    
    public boolean isPending() {
        return this.pending;
    }
    
    
    
    void clearData() {
        synchronized (this.attached) {
            this.attached.clear();
        }
    }
    
    
    
    
    void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }
    
    
    
    public Date getLastAction() {
        return this.lastAction;
    }
    
    
    
    void addEvent(HttpEvent event) {
        synchronized (this.events) {
            this.events.add(event);
            if (this.events.size() == EVENTS_TO_BUFFER) {
                this.events.poll();
            }
        }
    }
    
    
    
    boolean shouldKill() {
        return this.doKill;
    }
    
    
    
    @Override
    public Collection<HttpEvent> getEvents() {
        return Collections.unmodifiableCollection(this.events);
    }
    
    
    
    @Override
    public String getId() {
        return this.id;
    }
    
    
    
    @Override
    public void setExpirationDate(Date d) {
        this.expirationDate = d;
    }

    
    
    public Date getExpirationDate() {
        return this.expirationDate;
    }
    
    
    
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
    
    
    
    @Override
    public void set(String key, Object item) {
        synchronized (this.attached) {
            this.attached.put(key, item);
        }
    }
    
    

    @Override
    public void detach(String key) {
        synchronized (this.attached) {
            this.attached.remove(key);
        }
    }
    
    

    @Override
    public boolean isSet(String key) {
        synchronized (this.attached) {
            return this.attached.containsKey(key);
        }
    }

    
    
    @Override
    public Object getAttached(String key) {
        synchronized (this.attached) {
            return this.attached.get(key);
        }
    }
    
    
    
    @Override
    public Map<String, Object> getAttached() {
        return Collections.unmodifiableMap(this.attached);
    }
    

    
    @Override
    public TrafficInformation getTrafficInfo() {
        return this.trafficInfo;
    }



    @Override
    public void kill() {
        this.doKill = true;
    }



    @Override
    public synchronized void block(int milliseconds) {
        this.blockTime = milliseconds < 0 ? Long.MAX_VALUE : milliseconds;
        this.blockStamp = System.currentTimeMillis();
    }



    @Override
    public synchronized void unblock() {
        this.blockTime = 0;
    }



    @Override
    public synchronized boolean isBlocked() {
        return false;
//        long timeBlocked = System.currentTimeMillis() - this.blockStamp;
//        return timeBlocked >= this.blockTime;
    }
}
