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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.http.api.HttpEventHandler;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.ServerFactory;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpBinaryAnswer;
import de.skuzzle.polly.http.api.answers.HttpTemplateAnswer;



class HttpServerImpl implements HttpServer {
    
    private final static Random RANDOM = new Random();
    
    private final Map<String, List<HttpEventHandler>> handlers;
    private final Map<InetSocketAddress, HttpSessionImpl> ipToSession;
    private final Map<InetSocketAddress, HttpSessionImpl> pending;
    
    private final Map<String, HttpSessionImpl> idToSession;
    private final List<File> roots;
    private final Set<String> extensionWhitelist;
    private final AnswerHandlerMap handler;
    private final ServerFactory factory;
    private int sessionType;
    private int sessionLiveTime;
    
    
    private com.sun.net.httpserver.HttpServer server;
    
    private boolean isrunning;
    
    
    public HttpServerImpl(ServerFactory factory) {
        this.handlers = new URLMap<>();
        this.ipToSession = new HashMap<>();
        this.idToSession = new HashMap<>();
        this.extensionWhitelist = new HashSet<>();
        this.pending = new HashMap<>();
        this.handler = new AnswerHandlerMap();
        this.roots = new ArrayList<>();
        this.factory = factory;
        this.sessionType = SESSION_TYPE_COOKIE;
        
        // default handler
        this.addAnswerHandler(HttpBinaryAnswer.class, new SimpleBinaryAnswerHandler());
        this.addAnswerHandler(HttpTemplateAnswer.class, new TemplateAnswerHandler());
    }
    
    
    
    @Override
    public boolean isRunning() {
        return this.isrunning;
    }
    
    
    
    @Override
    public void start() throws IOException {
        if (this.isRunning()) {
            throw new IllegalStateException("server already running");
        }
        
        this.server = this.factory.create();
        this.server.createContext("/", new BasicEventHandler(this));
        this.server.start();
        this.isrunning = true;
    }
    
    
    
    @Override
    public void shutdown(int timeout) {
        if (!this.isRunning()) {
            throw new IllegalStateException("server not running");
        }
        this.server.stop(timeout);
        synchronized (this.idToSession) {
            this.idToSession.clear();
        }
        synchronized (this.ipToSession) {
            this.idToSession.clear();
        }
        this.server = null;
        this.isrunning = false;
    }
    
    
    
    @Override
    public HttpAnswerHandler getHandler(HttpAnswer answer) {
        final Class<?> cls = answer.getClass();
        return this.handler.resolve(cls);
    }
    
    
    
    public void addAnswerHandler(Class<?> answerType, HttpAnswerHandler handler) {
        this.handler.registerHandler(answerType, handler);
    }
    
    
    
    @Override
    public void addWebRoot(File file) {
        this.roots.add(file);
    }
    
    
    
    @Override
    public File resolveRelativeFile(String path) throws FileNotFoundException {
        synchronized (this.roots) {
        for (final File root : this.roots) {
            final File dest = new File(root, path);
            
            if (!dest.exists()) {
                continue;
            }
            
            final Path request = dest.toPath().normalize();
            final Path rootPath = root.toPath().normalize();
            final String absRequest = request.toString().toLowerCase();
            final String absRoot = rootPath.toString().toLowerCase();
            
            if (!absRequest.startsWith(absRoot)) {
                // skip this file because it is not relative to the template root
                continue;
            } else if (!this.extensionWhitelist.isEmpty()) {
                int i = absRequest.lastIndexOf(".");
                if (i == -1) {
                    // file has no whitelisted extension
                    continue;
                }
                
                final String ext = absRequest.substring(i).toLowerCase();
                if (!this.extensionWhitelist.contains(ext)) {
                    // file has no whitelisted extension
                    continue;
                }
            }
            if (!dest.exists()) {
                continue;
            }
            return dest;
        }
        }
        throw new FileNotFoundException(path);
    }
    
    

    @Override
    public void addHttpEventHandler(String url, HttpEventHandler handler) {
        List<HttpEventHandler> handlers = this.handlers.get(url);
        if (handlers == null) {
            handlers = new ArrayList<>();
            this.handlers.put(url, handlers);
        }
        handlers.add(handler);
    }

    
    
    @Override
    public void removeHttpEventHandler(String url, HttpEventHandler handler) {
        final List<HttpEventHandler> handlers = this.handlers.get(url);
        if (handlers == null) {
            return;
        }
        handlers.remove(handlers);
    }

    
    
    Map<String, List<HttpEventHandler>> getHandlers() {
        return Collections.unmodifiableMap(this.handlers);
    }



    @Override
    public int getSessionType() {
        return this.sessionType;
    }
    
    
    
    @Override
    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }
    
    
    
    @Override
    public int sessionLiveTime() {
        return this.sessionLiveTime;
    }

    
    
    @Override
    public void setSessionLiveTime(int sessionLiveTime) {
        this.sessionLiveTime = sessionLiveTime;
    }
    
    

    private final String createSessionId(InetSocketAddress ip) {
        final long id = RANDOM.nextLong() * System.currentTimeMillis() * 
            ip.hashCode();
        return Long.toHexString(id);
    }
    
    
    
    
    Map<InetSocketAddress, HttpSessionImpl> getTempStorage() {
        return this.pending;
    }
    
    
    
    synchronized HttpSessionImpl byID(HttpExchange t, Map<String, String> parameters) {
        synchronized (this.idToSession) {
            // id sent with the cookie or get parameters
            String id = parameters.get(SESSION_ID_NAME);
            
            if (id == null) {
                
                // client sent no id
                id = this.createSessionId(t.getRemoteAddress());
                
                final HttpSessionImpl temp = new HttpSessionImpl(
                    this, id);
                
                temp.setPending(true);
                this.idToSession.put(id, temp);
                return temp;
            }

            // client sent an id, so it can be removed from pending ones
            HttpSessionImpl session = this.idToSession.get(id);
            if (session == null) {
                session = new HttpSessionImpl(this, id);
                this.idToSession.put(id, session);
            }
            session.setPending(false);
            return session;
        }
    }
    
    
    
    HttpSessionImpl byIP(InetSocketAddress ip) {
        synchronized (this.ipToSession) {
            HttpSessionImpl session = this.ipToSession.get(ip);
            if (session == null) {
                final String id = this.createSessionId(ip);
                session = new HttpSessionImpl(this, id);
                this.ipToSession.put(ip, session);
            }
            return session;
        }
    }
    
    
    
    void cleanSessions() {
        final Date now = new Date();
        if (this.getSessionType() == SESSION_TYPE_COOKIE) {
            synchronized (this.idToSession) {
                final Iterator<HttpSessionImpl> it = this.idToSession.values().iterator();
                while (it.hasNext()) {
                    final HttpSessionImpl session = it.next();
                    
                    final Date exp = session.getExpirationDate() == null 
                        ? new Date(session.getTimestamp()) 
                        : session.getExpirationDate();
                        
                    if (session.shouldKill() || 
                            now.getTime() - exp.getTime() > this.sessionLiveTime) {
                        session.clearData();
                        it.remove();
                    }
                }
            }
        }
    }
    
    
    
    synchronized void killSession(HttpSessionImpl session) {
        session.block(-1);
        switch (this.getSessionType()) {
        case SESSION_TYPE_COOKIE:
        case SESSION_TYPE_GET:
            synchronized (this.idToSession) {
                this.idToSession.remove(session.getId());
            }
            break;
        case SESSION_TYPE_IP:
            synchronized (this.ipToSession) {
                this.ipToSession.remove(session);
            }
        }
    }
    
    
    
    @Override
    public Collection<HttpSession> getSessions() {
        final Collection<HttpSession> result = new ArrayList<>();
        switch (this.getSessionType()) {
        case SESSION_TYPE_COOKIE:
        case SESSION_TYPE_GET:
            synchronized (this.idToSession) {
                result.addAll(this.idToSession.values());
            }
            break;
        case SESSION_TYPE_IP:
            synchronized (this.ipToSession) {
                result.addAll(this.ipToSession.values());
            }
        }
        return Collections.unmodifiableCollection(result);
    }
    
    
    
    @Override
    public HttpSession findSession(String id) {
        switch (this.getSessionType()) {
        case SESSION_TYPE_COOKIE:
        case SESSION_TYPE_GET:
            synchronized (this.idToSession) {
                return this.idToSession.get(id);
            }
            
        default:
        case SESSION_TYPE_IP:
            synchronized (this.ipToSession) {
                for (final HttpSessionImpl session : this.ipToSession.values()) {
                    if (session.getId().equals(id)) {
                        return session;
                    }
                }
                return null;
            }
        }
    }
}
