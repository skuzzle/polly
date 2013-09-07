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

import java.util.Iterator;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.answers.HttpAnswer;


final class HttpEventHandlerChain implements HttpEventHandler {

    private final Iterator<HttpEventHandler> it;
    
    
    public HttpEventHandlerChain(Iterator<HttpEventHandler> it) {
        this.it = it;
    }
    
    
    
    @Override
    public HttpAnswer handleHttpEvent(String registered, HttpEvent e, 
            HttpEventHandler chain) throws HttpException {
        if (this.it.hasNext()) {
            return this.it.next().handleHttpEvent(registered, e, this);
        }
        return null;
    }
}
