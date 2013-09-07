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

/**
 * In contrast to {@link HttpEventHandler HttpEventHandlers}, a HttpEventListener only 
 * gets notified about incoming http events. It is not registered for events to a 
 * particular URL and can not produce an answer to be sent to the client. The 
 * {@link #onRequest(HttpEvent)} method will be called before the incoming event was 
 * passed to any handlers. 
 * 
 * @author Simon Taddiken
 */
public interface HttpEventListener {

    /**
     * This method will be called for any incoming http event if this listener was 
     * registered to a {@link HttpServer}.
     * 
     * @param e The incoming event.
     */
    public void onRequest(HttpEvent e);
}
