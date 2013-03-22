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

import de.skuzzle.polly.http.api.answers.HttpAnswer;


/**
 * A cookie is a key-value pair which is stored at the client side. To send
 * such a cookie to a client, it must simply be included in the 
 * {@link HttpAnswer} returned by your {@link HttpEventHandler}.
 * 
 * @author Simon Taddiken
 */
public class HttpCookie {
    
    private final String name;
    private final String value;
    private final String domain;
    private final int maxAge;
    
    
    
    public HttpCookie(String name, String value, String domain, int maxAge) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.maxAge = maxAge;
    }



    public HttpCookie(String name, String value, int maxAge) {
        this(name, value, null, maxAge);
    }
    
    
    
    public String getDomain() {
        return this.domain;
    }
    
    

    public String getName() {
        return this.name;
    }

    
    
    public String getValue() {
        return this.value;
    }

    
    
    public int getMaxAge() {
        return this.maxAge;
    }
}
