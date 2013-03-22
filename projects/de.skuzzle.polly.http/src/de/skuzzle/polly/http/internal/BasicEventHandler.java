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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.skuzzle.polly.http.api.HttpCookie;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpEventHandler;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpAnswers;



class BasicEventHandler implements HttpHandler {

    private final HttpServerImpl server;
    
    
    
    /**
     * Regex for splitting GET style parameters from the request uri
     */
    private final static Pattern GET_PARAMETERS = Pattern.compile(
        "(\\w+)=([^&]+)");
    
    
    
    public BasicEventHandler(HttpServerImpl server) {
        this.server = server;
    }
    
    
    
    private final void parseParameters(String in, Map<String, String> params) {
        final Matcher m = GET_PARAMETERS.matcher(in);
    
        while (m.find()) {
            final String key = in.substring(m.start(1), m.end(1));
            String value = in.substring(m.start(2), m.end(2));
            try {
                value = URLDecoder.decode(value, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (params.containsKey(key)) {
                final String val = params.get(key);
                params.put(key, val + ";" + value);
            } else {
                params.put(key, value);
            }
        }
    }



    private final void parsePostParameters(HttpExchange t, Map<String, String> result) 
            throws IOException {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(t.getRequestBody()));
            String line = null;
            while ((line = r.readLine()) != null) {
                if (!line.equals("")) {
                    parseParameters(line, result);
                }
            }
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }



    private final Map<String, String> parseCookies(HttpExchange t) {
        final List<String> cookies = t.getRequestHeaders().get("Cookie");
        if (cookies == null) {
            return Collections.emptyMap();
        }
        final Map<String, String> result = new HashMap<String, String>();
        for (final String cookie : cookies) {
            final String[] s = cookie.split("=");
            if (s.length != 2) {
                continue;
            }
            result.put(s[0], s[1]);
        }
        return result;
    }
    
    
    
    private final HttpEvent createEvent(HttpExchange t) throws IOException {
        final String requestUri = t.getRequestURI().toString();
        final Map<String, String> get = new HashMap<>();
        final Map<String, String> post = new HashMap<>();
        
        // extract GET parameters
        if (requestUri.contains("?")) {
            final String[] parts = requestUri.split("\\?", 2);
            this.parseParameters(parts[1], get);
        }
        // extract POST parameters
        this.parsePostParameters(t, post);

        // extract cookies
        final Map<String, String> cookies = this.parseCookies(t);
        
        // get session
        final HttpSessionImpl session;
        switch (this.server.getSessionType()) {
        case HttpServer.SESSION_TYPE_COOKIE:
            session = this.server.byID(t, cookies);
            break;
        case HttpServer.SESSION_TYPE_GET:
            session = this.server.byID(t, get);
            break;
        case HttpServer.SESSION_TYPE_IP:
            session = this.server.byIP(t.getRemoteAddress());
            break;
        default:
            // should not be reachable anyway
            throw new RuntimeException("illegal session type: " + 
                this.server.getSessionType());
        }
        
        final HttpEvent event = new HttpEventImpl(this.server, t, session, cookies, 
            get, post);
        return event;
    }
    
    
    
    @Override
    public void handle(HttpExchange t) throws IOException {
        
        final List<HttpEventHandler> handlers = this.server.getHandlers();
        final HttpEvent httpEvent = this.createEvent(t);
        final HttpSession session = httpEvent.getSession();
        final TrafficInformationImpl ti = 
            (TrafficInformationImpl) session.getTrafficInfo();
        
        // set stream to count incoming traffic
        final CountingInputStream in = new CountingInputStream(t.getRequestBody(), ti);
        t.setStreams(in, null);
        
        if (session.isBlocked()) {
            this.handleAnswer(
                HttpAnswers.createStringAnswer("Your session is temporarely blocked"), 
                t, httpEvent);
        }
        
        // handle the event
        for (final HttpEventHandler handler : handlers) {
            HttpAnswer answer = null;
            try {
                answer = handler.handleHttpEvent(httpEvent);
            } catch (HttpException e) {
                e.printStackTrace();
                // just skip this handler and pretend he was not able
                // to handle the event.
            }
            
            if (answer != null) {
                this.handleAnswer(answer, t, httpEvent);
                return;
            }
        }
        
        // event could not be handled
        this.handleAnswer(HttpAnswers.createStringAnswer("File not found"), 
            t, httpEvent);
    }

    
    
    private final void handleAnswer(HttpAnswer answer, HttpExchange t, 
            HttpEvent httpEvent) throws IOException {
        assert answer != null : "Answer must not be null";
        
        try {
            // add cookies as response header
            final Collection<HttpCookie> cookies = new ArrayList<>(answer.getCookies());
            
            if (httpEvent.getSession().getType() == HttpSession.SESSION_TYPE_TEMPORARY &&
                this.server.getSessionType() == HttpServer.SESSION_TYPE_COOKIE) {
                
                // if this is a temporary session, add a cookie with the new session id
                cookies.add(new HttpCookie(HttpServerImpl.SESSION_ID_NAME, 
                    httpEvent.getSession().getId(), this.server.sessionLiveTime()));
            }
            
            if (!cookies.isEmpty()) {
                t.getResponseHeaders().add("Set-Cookie", 
                    this.generateCookieString(answer));
            }
            t.sendResponseHeaders(answer.getResponseCode(), 0);
            
            // set stream to count outgoing traffic
            final HttpSession session = httpEvent.getSession();
            final TrafficInformationImpl ti = 
                (TrafficInformationImpl) session.getTrafficInfo();
            final CountingOutputStream out = new CountingOutputStream(
                t.getResponseBody(), ti);
            t.setStreams(null, out);
            
            // handle different types of answers
            final HttpAnswerHandler handler = this.server.getHandler(answer);
            if (handler == null) {
                // TODO: react
            }
            handler.handleAnswer(answer, httpEvent, out);
        } finally {
            t.close();
        }
    }
    
    
    
    private final String generateCookieString(HttpAnswer answer) {
        final StringBuilder b = new StringBuilder();
        final Iterator<HttpCookie> it = answer.getCookies().iterator();
        while (it.hasNext()) {
            final HttpCookie next = it.next();
            
            b.append(next.getName());
            b.append("=");
            b.append(next.getValue());
            b.append(";Version=1;Max-Age=");
            b.append(next.getMaxAge());
            if (next.getDomain() != null) {
                b.append(";Domain=");
                b.append(next.getDomain());
            }
            
            if (it.hasNext()) {
                b.append(",");
            }
        }
        return b.toString();
    }
}
