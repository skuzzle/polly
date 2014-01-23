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
     * Attaches an arbitrary object to this session.
     * 
     * @param key Key for the object.
     * @param item The object to attach.
     */
    public void set(String key, Object item);
    
    /**
     * Attaches an arbitrary object to this session. The attached object will  
     * automatically be removed after the provided caching time given in ms expired. If
     * the cache time is negative, the object will be stored forever. After being 
     * removed, the {@link #get(String) get} method will return <code>null</code> as if
     * that object never has existed.
     * 
     * <p>If there is already an object stored with the given key, it will be replaced 
     * and the task scheduled for deletion of that former object will be cancelled.</p>
     * 
     * @param key The key under which the object is stored.
     * @param item The object to store.
     * @param cacheTime The time in milliseconds after which the object will  
     *      automatically be removed. 
     */
    public void set(String key, Object item, long cacheTimeMs);
    
    public void detach(String key);
    
    public boolean isSet(String key);
    
    /**
     * Gets the object assigned to the provided <tt>key</tt> or <code>null</code> if no 
     * such object exists. If the object exists, this method will perform an unsafe
     * type cast to the caller's target type. When used wrong, this will throw a 
     * {@link ClassCastException}.
     * 
     * @param key The key of the object to retrieve
     * @return The attached object or <code>null</code>.
     */
    public <T> T get(String key);
    
    /**
     * Gets the object assigned to the provided <tt>key</tt> or <tt>backup</tt> if no such
     * object is attached to this session. The backup element will <b>not</b> be stored 
     * in the session.
     *  
     * @param key The key of the object to retrieve
     * @param backup The element returned if no object is currently attached with 
     *          provided key.
     * @return The attached object or the provided backup.
     */
    public <T> T get(String key, T backup);
    
    /**
     * Gets the object assigned to the provided <tt>key</tt> or <code>null</code> if no 
     * such object exists. Additionally, if an object existed with the givem key, it 
     * will be removed from this session.
     * 
     * @param key The key of the object to retrieve
     * @return The attached object or <code>null</code>
     */
    public <T> T getOnce(String key);
    
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
