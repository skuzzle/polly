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
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;



public class DefaultServerFactory implements ServerFactory {

    protected final int port;
    protected final ExecutorService executor;
    
    
    
    public DefaultServerFactory(int port, ExecutorService executor) {
        this.port = port;
        this.executor = executor;
    }
    
    
    
    public DefaultServerFactory(int port) {
        this(port, Executors.newCachedThreadPool());
    }
    
    
    
    @Override
    public HttpServer create() throws IOException {
        com.sun.net.httpserver.HttpServer server = 
            com.sun.net.httpserver.HttpServer.create(
                new InetSocketAddress(this.port), 5);
        server.setExecutor(this.executor);
        return server;
    }
}
