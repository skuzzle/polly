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
package de.skuzzle.polly.http.api.answers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.skuzzle.polly.http.api.HttpCookie;


public class AbstractHttpAnswer extends HttpAnswer {

    private int responseCode;
    private final Map<String, List<String>> headers;
    private final Set<HttpCookie> cookies;
    
    
    public AbstractHttpAnswer(int responseCode) {
        this.responseCode = responseCode;
        this.headers = new HashMap<String, List<String>>();
        this.cookies = new HashSet<HttpCookie>();
    }
    
    
    
    @Override
    public int getResponseCode() {
        return this.responseCode;
    }

    
    
    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return this.headers;
    }

    
    
    @Override
    public HttpAnswer redirectTo(String url) {
        this.responseCode = 303;
        this.addHeader("Location", url);
        return this;
    }
    
    
    
    @Override
    public HttpAnswer addHeader(String name, String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            this.headers.put(name, values);
        }
        values.add(value);
        return this;
    }
    
    
    
    @Override
    public Collection<HttpCookie> getCookies() {
        return this.cookies;
    }
    
    
    
    @Override
    public HttpAnswer addCookie(HttpCookie cookie) {
        this.cookies.add(cookie);
        return this;
    }
    
    
    
    @Override
    public HttpAnswer addCookie(String name, String value, int maxAge) {
        return this.addCookie(new HttpCookie(name, value, maxAge));
    }
}
