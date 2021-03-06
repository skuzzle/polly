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

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.Map;

/**
 * A HttpEvent occurs if the {@link HttpServer} receives any http request from a 
 * remote client. This event then carries all the information about the request. You
 * can react on events by 
 * {@link HttpServer#registerHttpEventHandler(HttpEventHandler) registering} a 
 * {@link HttpEventHandler} with your {@link HttpServer}. The server can create exactly
 * one answer which is sent back to the client per incoming event. If you chose to 
 * {@link #discard() discard} an event, the server will not generate any answer and will
 * simply kill the current connection to the client.
 * 
 * <p>Along with an event comes the {@link HttpSession http session}. A session is a 
 * unique object that persists over multiple connections from the same client.</p>
 * 
 * @author Simon Taddiken
 * @see HttpServer
 */
public interface HttpEvent {
    
    /**
     * Simple HTTP request mode.
     * @author Simon Taddiken
     */
    public enum RequestMode {
        GET, POST;
    }
    
    
    public interface Listener {
        public void action(HttpEvent event);
    }
    
    
    
    /**
     * Gets the body of the incoming request.
     * @return The request body.
     */
    public String getRequestBody();
    
    /**
     * Gets the time of when this event was created.
     * 
     * @return Event creation time.
     */
    public Date getEventTime() ;
    
    /**
     * Gets the {@link HttpServer} which received the event.
     * 
     * @return The HttpServer.
     */
    public HttpServer getSource();
    
    /**
     * Gets the mode of this event.
     * 
     * @return Whether this is a POST or GET event.
     */
    public RequestMode getMode();
    
    /**
     * Gets the requested URI.
     * 
     * @return The requested URI.
     */
    public URI getRequestURI();
    
    /**
     * Gets the requested URI as String and without any GET parameters.
     * 
     * @return The requested URI without get parameters.
     */
    public String getPlainUri();
    
    /**
     * Gets the ip address of the client that sent this request.
     * 
     * @return The client's ip address.
     */
    public InetSocketAddress getClientIP();
    
    /**
     * Gets the value of a parameter in a GET request. If multiple values are assigned
     * to this key, they will be ';' separated in the result string. If no value is 
     * associated with the given key, <code>null</code> is returned.
     * 
     * @param key Name of the GET parameter.
     * @return The value associated with that key, or <code>null</code>.
     */
    public String get(String key);
    
    /**
     * Gets the escaped value of a parameter in a GET request. If multiple values are 
     * assigned to this key, they will be ';' separated in the result string. If no value 
     * is associated with the given key, <code>null</code> is returned.
     * 
     * @param key Name of the GET parameter.
     * @return The value associated with that key, or <code>null</code>.
     */
    public String getEscaped(String key);
    
    /**
     * Gets the value of a parameter in a POST request. If multiple values are assigned
     * to this key, they will be ';' separated in the result string. If no value is 
     * associated with the given key, <code>null</code> is returned.
     * 
     * @param key Name of the POST parameter.
     * @return The value associated with that key, or <code>null</code>.
     */
    public String post(String key);
    
    /**
     * Gets the escaped value of a parameter in a POST request. If multiple values are 
     * assigned to this key, they will be ';' separated in the result string. If no value 
     * is associated with the given key, <code>null</code> is returned.
     * 
     * @param key Name of the POST parameter.
     * @return The value associated with that key, or <code>null</code>.
     */
    public String postEscaped(String key);
    
    /**
     * Gets a map view of all parameters in this request. The result is a map which 
     * contains all associations of keys to values from the cookies, the GET and the 
     * POST parameters. Note that the result will be read-only. 
     * 
     * <p>If a key occurs twice, the associated values will be joined into a single 
     * string by separating them by ';'. The so created result will only contain 
     * distinct values, the order in which they occur in the string is undefined.</p>
     * 
     * @return Map view of all parameters associated with this request.
     */
    public Map<String, String> parameterMap();
    
    /**
     * Gets a map view of either the post or get parameters in this request. Note that 
     * the result is read-only.
     * 
     * @param mode Request type of parameters to retrieve.
     * @return Read-only map of parameters.
     */
    public Map<String, String> parameterMap(RequestMode mode);
    
    /**
     * Gets a map view of all provided POST parameters of this request. Note that the
     * result is read-only.
     * 
     * @return Read-only map of all POST parameters.
     */
    public Map<String, String> postMap();
    
    /**
     * Gets a map view of all provided GET parameters of this request. Note that the
     * result is read-only.
     * 
     * @return Read-only map of all GET parameters.
     */
    public Map<String, String> getMap();
    
    /**
     * Gets a map view of both GET and POST parameters provided by this request. The 
     * resulting map contains all associations from the GET and the POST parameters. Note 
     * that the result will be read only. 
     * 
     * <p>If a key occurs twice, the associated values will be joined into a single 
     * string by separating them by ';'. The so created result will only contain 
     * distinct values, the order in which they occur in the string is undefined.</p>
     * 
     * @return Read-only map of GET and POST parameters.
     */
    public Map<String, String> postGetMap();
    
    /**
     * Gets the session that belongs to this request. This is a unique object which 
     * persists over different requests from the same client.
     * 
     * @return The session of this event.
     */
    public HttpSession getSession();
    
    /**
     * Discards this event. That is, its current connection to the client will be 
     * closed. If discarded during a {@link HttpAnswer} is processed, the process will
     * fail. Discarding an event will not always raise a close event.
     */
    public void discard();
    
    
    /**
     * Whether {@link #discard()} was called for this event. Any further processing of
     * this event is likely to fail if this method returns <code>true</code>.
     * @return Whether this event was discarded.
     */
    public boolean isDiscarded();
    
    
    /**
     * Registers a listener which will be notified when the server sent an answer to this
     * event to the client is then closed in a natural manner. Discarding an event is no
     * natural manner and may thus not necessarily raise an 'onClose' event.
     * 
     * @param listener The listener to register.
     */
    public void onClose(Listener listener);
}
