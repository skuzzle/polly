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

import java.util.Objects;



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
    private final String path;
    private final int maxAge;
    
    
    
    public HttpCookie(String name, String value, String domain, int maxAge) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.maxAge = maxAge;
        this.path = "/";
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
    
    
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null &&
            obj instanceof HttpCookie &&
            ((HttpCookie) obj).name.equals(this.name);
    }
    
    
    
    /**
     * Generates a HTTP response header entry for this cookie.
     * 
     * @return This cookie formatted to a response header string.
     */
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.getName());
        b.append("=");
        b.append(this.getValue());
        b.append(";Version=1;Max-Age=");
        b.append(this.getMaxAge());
        b.append(";Path=");
        b.append(this.path);
        if (this.domain != null) {
            b.append(";Domain=");
            b.append(this.domain);
        }
        return b.toString();
    }
}
