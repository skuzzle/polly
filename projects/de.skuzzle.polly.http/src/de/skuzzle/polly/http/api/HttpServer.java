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

import java.io.IOException;
import java.util.Collection;

import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;



public interface HttpServer {
    
    /** Name of the cookie or GET parameter for the session id */
    public final static String SESSION_ID_NAME = "sessionID";
    
    public final static int SESSION_TYPE_COOKIE = 0;
    
    public final static int SESSION_TYPE_IP = 1;
    
    public final static int SESSION_TYPE_GET = 2;

    public static final int SESSION_HISTORY_SIZE = 20;
    

    
    /**
     * Determines whether the server is currently running.
     * 
     * @return Whether the server is currently running.
     */
    public boolean isRunning();
    
    /**
     * Starts the http server using the {@link ServerFactory} supplied to this class. 
     * If the server is already running, an {@link IllegalStateException} will be thrown.
     * 
     * @throws IOException If running the server fails.
     */
    public void start() throws IOException;
    
    /**
     * Stops the server if it is currently running and clears all active sessions. If the 
     * server is not running, an {@link IllegalStateException} will be thrown.
     * 
     * @param timeout Timeout to wait for active event handlers in seconds.
     */
    public void shutdown(int timeout);
    
    /**
     * Gets how sessions of this server are managed. Either of 
     * {@link #SESSION_TYPE_COOKIE}, {@link #SESSION_TYPE_GET} or 
     * {@link #SESSION_TYPE_IP}.
     * 
     * @return The current session type.
     */
    public int getSessionType();
    
    /**
     * Sets how sessions are managed by this server. Either of 
     * {@link #SESSION_TYPE_COOKIE}, {@link #SESSION_TYPE_GET} or 
     * {@link #SESSION_TYPE_IP}.
     * 
     * @param sessionType The new session type.
     */
    public void setSessionType(int sessionType);
    
    /**
     * Time in milliseconds after which a session should expire.
     * 
     * @return The session expiration time.
     */
    public int sessionLiveTime();
    
    /**
     * Sets the time in milliseconds of how long sessions may stay alive before they
     * expired.
     * 
     * @param liveTime New session live time.
     */
    public void setSessionLiveTime(int liveTime);
    
    /**
     * Finds a session with the provided ID. Will return <code>null</code> if no such 
     * ID exists.
     * 
     * @param id ID of the session to retrieve.
     * @return The session or <code>null</code>.
     */
    public HttpSession findSession(String id);
    
    /**
     * Registers the given handler which then might be notified about new http events if
     * the specified URL is requested by a client. Note that multiple handlers can be 
     * assigned to an URL.
     * 
     * @param url URL to call in order to raise the registered event.
     * @param handler The event handler to register.
     */
    public void addHttpEventHandler(String url, HttpEventHandler handler);
    
    /**
     * Removes the given handler.
     * 
     * @param url URL to remove the handler from.
     * @param handler The handler to remove.
     */
    public void removeHttpEventHandler(String url, HttpEventHandler handler);
    
    /**
     * Adds a listener which gets notified on every incoming {@link HttpEvent}.
     * @param listener Listener to add.
     */
    public void addHttpEventListener(HttpEventListener listener);
    
    /**
     * Removes a listener.
     * @param listener The listener to remove.
     */
    public void removeHttpEventListener(HttpEventListener listener);
    
    /**
     * Gets a collection of all URLs for which a handler exists.
     * @return Collection of all available URLs on this server.
     */
    public Collection<String> getURLs();
    
    /**
     * Gets a collection of previous sessions that already have been invalidated.
     * 
     * @return Collection of outdated sessions.
     */
    public Collection<HttpSession> getSessionHistory();

    /**
     * <p>This method will resolve a suitable {@link HttpAnswerHandler} for the given 
     * answer. If no handler for the concrete type of the answer is found, this method 
     * tries to find a handler for any of the answer's super types. If still none could 
     * be found, <code>null</code> is returned.</p>
     * 
     * <p>Custom answer handler can be provided using <code>addAnswerHandler()</code>
     * method. By default, handlers for {@link HttpTemplateAnswer} and 
     * {@link HttpBinaryAnswer} exist.</p>
     *  
     * @param answer The answer for which a handler should be resolved.
     * @return The resolved handler or <code>null</code> if none was found.
     * @see #registerHandler(Class, HttpAnswerHandler)
     */
    public HttpAnswerHandler getHandler(HttpAnswer answer);
    
    /**
     * Registers a {@link HttpAnswerHandler answer handler} as default handler for 
     * answers of the given class's type. The given handler will also be used for
     * all sub types of the specified class if no more concrete handler is registered.
     *  
     * @param answerType Type of the answer to handle. This should be a sub class of
     *          {@link HttpAnswer}.
     * @param handler The handler which can handle answers of the given type.
     */
    public void setAnswerHandler(Class<?> answerType, HttpAnswerHandler handler);
    
    /**
     * Gets a read-only collection of all current sessions.
     * @return Collection of sessions.
     */
    public Collection<HttpSession> getSessions();

    /**
     * Gets the overall traffic information for this server instance.
     * 
     * @return The overall traffic information.
     */
    public TrafficInformation getTraffic();
}
