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


public interface HttpSession {
    
    public final static int SESSION_TYPE_TEMPORARY = 0;
    
    public final static int SESSION_TYPE_COOKIE = 1;
    
    /**
     * Gets a timestamp of when this session was created.
     * 
     * @return Time when this session was created.
     */
    public long getTimestamp();
    
    public int getType();
    
    /**
     * Gets the unique id of this session.
     * 
     * @return The unique session id.
     */
    public String getId();

    public void attach(String key, Object item);
    
    public void detach(String key);
    
    public boolean isAttached(String key);
    
    public void getAttached(String key);
    
    /**
     * Gets information on how much bytes has been sent and received over this session.
     * 
     * @return A TrafficInformation object.
     */
    public TrafficInformation getTrafficInfo();
    
    
    public void kill();
    
    /**
     * <p>Blocks this session for the given amount of milliseconds. 
     * {@link HttpEvent HttpEvents} incoming over a blocked session will not be handled
     * by the {@link HttpServer HttpServers} event handlers. Instead a simple message
     * will be sent back saying that the connection is currently blocked.</p>
     * 
     * <p>Blocking and unblocking sessions is thread-safe.</p>
     * 
     * @param milliseconds Time of how long the session should be blocked. Specify a 
     *          negative value if you want to block until its manually unblocked.
     * @see #unblock()
     * @see #isBlocked()
     */
    public void block(int milliseconds);
    
    /**
     * Unblocks the session right now. If it is not blocked, calling this method will
     * have no effect.
     * 
     * <p>Blocking and unblocking sessions is thread-safe.</p>
     * 
     * @see #block(int)
     * @see #isBlocked()
     */
    public void unblock();
    
    /**
     * Determines whether this session is currently blocked.
     * 
     * @return Whether the session is currently blocked.
     * @see #block(int)
     * @see #unblock()
     */
    public boolean isBlocked();
}
