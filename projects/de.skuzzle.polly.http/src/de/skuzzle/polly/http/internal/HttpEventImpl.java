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

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpSession;


class HttpEventImpl implements HttpEvent {

    private final HttpServer source;
    private final URI requestUri;
    private final String plainUri;
    private final InetSocketAddress clientIp;
    private final HttpSession session;
    private final Map<String, String> cookies;
    private final Map<String, String> get;
    private final Map<String, String> post;
    private final RequestMode mode;
    private Map<String, String> combinedParameters;
    private Map<String, String> postGet;
    private final Date creationTime;
    
    
    public HttpEventImpl(HttpServer source, RequestMode mode, HttpExchange t, 
        String plainUri, HttpSession session, Map<String, String> cookies, 
        Map<String, String> get, Map<String, String> post) {
        
        this.source = source;
        this.mode = mode;
        this.requestUri = t.getRequestURI();
        this.plainUri = plainUri;
        this.clientIp = t.getRemoteAddress();
        this.session = session;
        
        this.cookies = Collections.unmodifiableMap(cookies);
        this.get = Collections.unmodifiableMap(get);
        this.post = Collections.unmodifiableMap(post);
        
        this.creationTime = new Date();
    }
    
    
    
    @Override
    public Date getEventTime() {
        return this.creationTime;
    }
    
    
    
    @Override
    public HttpServer getSource() {
        return this.source;
    }
    
    
    
    @Override
    public RequestMode getMode() {
        return this.mode;
    }
    
    

    @Override
    public URI getRequestURI() {
        return this.requestUri;
    }

    
    
    @Override
    public String getPlainUri() {
        return this.plainUri;
    }
    
    
    
    @Override
    public InetSocketAddress getClientIP() {
        return this.clientIp;
    }

    
    
    @Override
    public String get(String key) {
        return this.get.get(key);
    }

    
    
    @Override
    public String post(String key) {
        return this.post.get(key);
    }

    
    
    @Override
    public Map<String, String> parameterMap() {
        synchronized (this) {
        if (this.combinedParameters == null) {
            this.combinedParameters = new HashMap<>(this.get.size() + this.post.size() + 
                this.cookies.size());
            
            this.combinedParameters.putAll(this.cookies);
            join(this.combinedParameters, this.get);
            join(this.combinedParameters, this.post);
            this.combinedParameters = Collections.unmodifiableMap(
                this.combinedParameters);
        }
        return this.combinedParameters;
        }
    }
    
    
    
    @Override
    public Map<String, String> parameterMap(RequestMode mode) {
        switch (mode) {
        case GET: return this.getMap();
        case POST: return this.postMap();
        default:
            throw new IllegalStateException("unknown mode");
        }
    }
    
    
    
    @Override
    public Map<String, String> postGetMap() {
        synchronized (this) {
        if (this.postGet == null) {
            this.postGet = new HashMap<>(this.get.size() + this.post.size());
            
            this.postGet.putAll(this.get);
            join(this.postGet, this.post);
            this.postGet = Collections.unmodifiableMap(
                this.postGet);
        }
        return this.postGet;
        }
    }
    
    
    
    private final static void join(Map<String, String> target, 
            Map<String, String> source) {
        for (final Entry<String, String> se : source.entrySet()) {
            String targetValue = target.get(se.getKey());
            if (targetValue != null && !targetValue.contains(se.getValue())) {
                targetValue = targetValue + ";" + se.getValue();
            } else {
                targetValue = se.getValue();
            }
            target.put(se.getKey(), targetValue);
        }
    }
    

    
    @Override
    public Map<String, String> postMap() {
        return this.post;
    }

    
    
    @Override
    public Map<String, String> getMap() {
        return this.get;
    }
    
    

    @Override
    public HttpSession getSession() {
        return this.session;
    }    
}
