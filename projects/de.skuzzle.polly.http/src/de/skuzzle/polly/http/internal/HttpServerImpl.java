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
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.http.api.HttpEventHandler;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpBinaryAnswer;
import de.skuzzle.polly.http.api.answers.HttpTemplateAnswer;



class HttpServerImpl implements HttpServer {
    
    final static String SESSION_ID_NAME = "sessionID";
    
    private final static Random RANDOM = new Random();
    
    private final List<HttpEventHandler> handlers;
    private final Map<InetSocketAddress, HttpSessionImpl> ipToSession;
    private final Map<String, HttpSessionImpl> idToSession;
    private final List<File> roots;
    private final Set<String> extensionWhitelist;
    private final AnswerHandlerMap handler;
    
    
    
    public HttpServerImpl() {
        this.handlers = new ArrayList<>();
        this.ipToSession = new HashMap<>();
        this.idToSession = new HashMap<>();
        this.extensionWhitelist = new HashSet<>();
        this.handler = new AnswerHandlerMap();
        this.roots = new ArrayList<>();
        
        // default handler
        this.registerHandler(HttpBinaryAnswer.class, new SimpleBinaryAnswerHandler());
        this.registerHandler(HttpTemplateAnswer.class, new TemplateAnswerHandler());
    }
    
    
    
    @Override
    public HttpAnswerHandler getHandler(HttpAnswer answer) {
        return this.handler.resolve(answer.getClass());
    }
    
    
    
    public void registerHandler(Class<?> answerType, HttpAnswerHandler handler) {
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
                
                final String ext = absRequest.substring(i);
                if (!this.extensionWhitelist.contains(ext)) {
                    // file has no whitelisted extension
                    continue;
                }
            }
            
            return dest;
        }
        }
        throw new FileNotFoundException(path);
    }
    
    

    @Override
    public void registerHttpEventHandler(HttpEventHandler handler) {
        this.handlers.add(handler);
    }

    
    
    @Override
    public void unregisterHttpEventHandler(HttpEventHandler handler) {
        this.handlers.remove(handler);
    }

    
    
    List<HttpEventHandler> getHandlers() {
        return Collections.unmodifiableList(this.handlers);
    }



    @Override
    public int getSessionType() {
        return 0;
    }
    
    
    
    @Override
    public int sessionLiveTime() {
        return 0;
    }

    

    private final String createSessionId(InetSocketAddress ip) {
        final long id = RANDOM.nextLong() * System.currentTimeMillis() * 
            ip.hashCode();
        return Long.toHexString(id);
    }
    
    
    
    HttpSessionImpl byID(HttpExchange t, Map<String, String> parameters) {
        synchronized (this.idToSession) {
            String id = parameters.get(SESSION_ID_NAME);
            if (id == null) {
                // No session id was sent, so client did not get one until now.
                // create temporary session. So next time the client sends something,
                // it will have an id assigned
                id = this.createSessionId(t.getRemoteAddress());
                return new HttpSessionImpl(id, HttpSession.SESSION_TYPE_TEMPORARY);
            }

            HttpSessionImpl session = this.idToSession.get(id);
            if (session == null) {
                session = new HttpSessionImpl(id, HttpSession.SESSION_TYPE_COOKIE);
                this.idToSession.put(id, session);
            }
            return session;
        }
    }
    
    
    
    HttpSessionImpl byIP(InetSocketAddress ip) {
        synchronized (this.ipToSession) {
            HttpSessionImpl session = this.ipToSession.get(ip);
            if (session == null) {
                final String id = this.createSessionId(ip);
                session = new HttpSessionImpl(id, SESSION_TYPE_IP);
                this.ipToSession.put(ip, session);
            }
            return session;
        }
    }
}
