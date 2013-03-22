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

    private final String id;
    private final long timestamp;
    private final int type;
    private final Map<String, Object> attached;
    private final TrafficInformation trafficInfo;
    
    
    
    public HttpSessionImpl(String id, int type) {
        if (type != SESSION_TYPE_COOKIE && type != SESSION_TYPE_TEMPORARY) {
            throw new IllegalArgumentException("illegal session type");
        }
        
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.attached = new HashMap<>();
        this.trafficInfo = new TrafficInformationImpl();
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
        this.attached.put(key, item);
    }
    
    

    @Override
    public void detach(String key) {
        this.attached.remove(key);
    }
    
    

    @Override
    public boolean isAttached(String key) {
        return this.attached.containsKey(key);
    }

    
    
    @Override
    public void getAttached(String key) {
        this.attached.get(key);
    }
    
    

    @Override
    public TrafficInformation getTrafficInfo() {
        return this.trafficInfo;
    }
}
