/*
 * Copyright 2013 Simon Taddiken
 * 
 * This file is part of Polly HTTP API.
 * 
 * Polly HTTP API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Polly HTTP API is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Polly HTTP API. If not, see http://www.gnu.org/licenses/.
 */
package de.skuzzle.polly.http.api.handler;

import java.io.FileNotFoundException;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.answers.HttpAnswer;

abstract class AbstractFileEventHandler implements HttpEventHandler {

    /**
     * Whether additional event handlers that were registered for the same URL
     * should be executed before this handler is executed
     */
    protected final boolean executeFollowers;

    /**
     * Value for Cache-Control header field. Maximum amount of seconds which a
     * file sent by this handler is cached on the client side.
     */
    protected int maxAge;

    /** Response code for all answers produced by this handler */
    protected  final int responseCode;


    
    public AbstractFileEventHandler(int responseCode, boolean executeFollowers) {
        this.executeFollowers = executeFollowers;
        this.responseCode = responseCode;
        this.maxAge = 86000;
    }
    
    

    /**
     * Gets the 'max-age' cache control value.
     * 
     * @return The max-age value.
     */
    public int getMaxAge() {
        return this.maxAge;
    }



    /**
     * Sets the 'max-age' cache control value. This is the maximum amount of
     * seconds which a file is cached on the client side before it will be
     * requested again.
     * 
     * @param maxAge
     *            Maximum amount of seconds to cache files.
     */
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }



    @Override
    public final HttpAnswer handleHttpEvent(String registered, HttpEvent e, 
            HttpEventHandler next) throws HttpException {
        
        final HttpAnswer backup;
        if (this.executeFollowers) {
            backup = next.handleHttpEvent(registered, e, next);
        } else {
            backup = null;
        }
        
        try {
            final HttpAnswer answer = this.handleHttpEvent(registered, e);
            if (answer != null) {
                answer.addHeader("Cache-Control", "max-age=" + this.maxAge);
            }
            return answer;
        } catch (FileNotFoundException e1) {
            if (backup != null) {
                return backup;
            }
            throw new HttpException(e1);
        } catch (HttpException e1) {
            throw e1;
        }
    }
    
    
    
    protected abstract HttpAnswer handleHttpEvent(String registered, HttpEvent e) 
            throws FileNotFoundException, HttpException;      
}
