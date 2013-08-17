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

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.TrafficInformation;


class HttpSessionImpl implements HttpSession {

    private final HttpServerImpl server;
    private final String id;
    private final long timestamp;
    private final int type;
    private final Map<String, Object> attached;
    private final TrafficInformation trafficInfo;
    
    /** Timestamp at which blocking the session started */
    private long blockStamp;
    
    /** Time in ms how long this session should be blocked */
    private long blockTime;
    
    
    
    public HttpSessionImpl(HttpServerImpl server, String id, int type) {
        if (type != SESSION_TYPE_COOKIE && type != SESSION_TYPE_TEMPORARY) {
            throw new IllegalArgumentException("illegal session type");
        }
        
        this.server = server;
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.attached = new HashMap<>();
        this.trafficInfo = new TrafficInformationImpl();
        
        // make sure session is initially unblocked
        this.blockStamp = Long.MAX_VALUE;
        this.blockTime = 0;
    }
    
    
    
    @Override
    public String getId() {
        return this.id;
    }
    
    
    
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
    
    

    @Override
    public int getType() {
        return this.type;
    }

    
    
    @Override
    public void attach(String key, Object item) {
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
    public boolean isAttached(String key) {
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
    public TrafficInformation getTrafficInfo() {
        return this.trafficInfo;
    }



    @Override
    public void kill() {
        this.server.killSession(this);
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
