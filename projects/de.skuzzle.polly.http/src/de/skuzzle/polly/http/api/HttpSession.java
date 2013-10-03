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
package de.skuzzle.polly.http.api;

import java.util.Date;
import java.util.Deque;
import java.util.Map;


public interface HttpSession {
    
    /**
     * Gets a timestamp of when this session was created.
     * 
     * @return Time when this session was created.
     */
    public long getTimestamp();
    
    /**
     * Sets the expiration date of this session. If this session was assigned an 
     * expiration date using this method, it will be removed by the server at the next
     * request after the assigned date. If it has no expiration date set, the server will
     * remove the session automatically after a configurable amount of time.
     * 
     * @param d The new expiration date of this session.
     * @see HttpServer#setSessionLiveTime(int)
     */
    public void setExpirationDate(Date d);
    
    /**
     * Gets the unique id of this session.
     * 
     * @return The unique session id.
     */
    public String getId();

    /**
     * Attaches an abitrary object to this session.
     * 
     * @param key Key for the object.
     * @param item The object to attach.
     */
    public void set(String key, Object item);
    
    public void detach(String key);
    
    public boolean isSet(String key);
    
    public Object getAttached(String key);
    
    /**
     * Gets information on how much bytes has been sent and received over this session.
     * 
     * @return A TrafficInformation object.
     */
    public TrafficInformation getTrafficInfo();
    
    /**
     * Kills this session. The next time a request is sent over this
     * connection, the cookie (if any) on the client side will be removed. 
     */
    public void kill();

    /**
     * Gets a collection of the last few HTTP events that were raised by this session.
     * 
     * @return Collection of http events.
     */
    public Deque<HttpEvent> getEvents();
    
    /**
     * Gets a read-only map of all attached objects.
     * @return Map of objects attached to this session.
     */
    public Map<String, Object> getAttached();

    /**
     * Gets the expiration date of this session.
     * 
     * @return The expiration date.
     */
    public Date getExpirationDate();
    
    /**
     * Gets the date of the last event which was raised by this session.
     * @return The last action date.
     */
    public Date getLastActionDate();
}
