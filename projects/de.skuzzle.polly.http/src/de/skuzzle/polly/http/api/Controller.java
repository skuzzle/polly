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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Controllers are used in conjunction with 
 * {@link HttpServletServer HttpServletServers}. They provide annotated handler methods 
 * for various {@link HttpEvent HttpEvents}. This is a quick example for a Controller 
 * which may handle a user database:</p>
 * 
 * <pre>
 * public class UserController extends Controller {
 *     private Persistence p; // Some abstract persistence manager for this sample
 *     
 *     public UserController(Persistence p) {
 *         this.p = p;
 *     }
 *     
 *     &#64;Override
 *     protected Controller createInstance() {
 *         return new UserController(p);
 *     }
 *     
 *     
 *     &#64;Post("/addUser")
 *     public HttpAnswer addUser(&#64;Param("name) String name) {
 *         User user = new User(name);
 *         this.p.persist(user);
 *         HttpAnswers.createStringAnswer("User added");
 *     }
 *     
 *     &#64;Post("/deleteUser")
 *     public void deleteUser(&#64;Param("name") String name) {
 *         User user = this.p.find(name);
 *         this.p.delete(user);
 *         HttpAnswers.createStringAnswer("User deleted");
 *     }
 * }
 * </pre>
 * 
 * When registering this controller with an instance of {@link HttpServletServer} it will
 * automatically call the annotated methods if a <tt>POST</tt> request to their URL
 * occurs.
 * 
 * @author Simon Taddiken
 */
public abstract class Controller {

    private HttpEvent event;
    private Map<String, String> myHandlers;
    private String handlerPrefix;
    
    
    
    public Controller() {
        this.handlerPrefix = "";
        this.myHandlers = new HashMap<>();
    }
    
    
    
    public void setHandlerPrefix(String handlerPrefix) {
        this.handlerPrefix = handlerPrefix;
    }
    
    
    
    
    public String getHandlerPrefix() {
        return this.handlerPrefix;
    }
    
    
    
    public void putHandledURL(Map<String, String> target, String handlerName, 
            String url) {
        target.put(this.handlerPrefix + handlerName, url);
        this.myHandlers.put(this.handlerPrefix + handlerName, url);
    }
    
    
    
    /**
     * Gets the currently handled HttpEvent.
     * 
     * @return The HttpEvent.
     */
    protected final HttpEvent getEvent() {
        return this.event;
    }
    
    
    
    /**
     * Gets the HttpServer from which the current handled event originates. This is just
     * a call wrapper for <code>getEvent().getSource()</code>.
     * 
     * @return The HttpServer instance.
     */
    protected final HttpServletServer getServer() {
        return (HttpServletServer) this.getEvent().getSource();
    }
    
    
    
    protected HttpSession getSession() {
        return this.getEvent().getSession();
    }
    
    
    
    /**
     * Gets a map which contains as values all request URLs that are handled by this 
     * controller. The key for each URL will be the name of the method which is annotated
     * to handle it optionally preceded by a prefix which can be configured in 
     * {@link HttpServletServer}.
     * @return URL map of this controller.
     */
    public Map<String, String> getMyHandlers() {
        return Collections.unmodifiableMap(this.myHandlers);
    }
    
    
    
    /**
     * This method is intended to create an exact copy of this controller. It will be
     * used by {@link #bind(HttpEvent)}.
     * 
     * @return A new instance of this controller.
     */
    protected abstract Controller createInstance();
    
    
    
    /**
     * Creates a copy of this controller and binds it to the provided {@link HttpEvent}.
     * 
     * @param e The event.
     * @return A new instance of this controller.
     */
    public final Controller bind(HttpEvent e) {
        final Controller result = this.createInstance();
        result.event = e;
        result.myHandlers = this.myHandlers;
        return result;
    }
}
