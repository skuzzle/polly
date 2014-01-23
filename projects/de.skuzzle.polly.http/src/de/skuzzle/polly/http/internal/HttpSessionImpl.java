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
package de.skuzzle.polly.http.internal;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpSession;

class HttpSessionImpl implements HttpSession {

    private final static int EVENTS_TO_BUFFER = 20;

    private final String id;
    private final long timestamp;
    private final Map<String, Object> attached;
    private final TrafficInformationImpl trafficInfo;
    private final Deque<HttpEvent> events;
    private Date lastAction;
    private boolean doKill;

    private Date expirationDate;
    private boolean pending;



    public HttpSessionImpl(HttpServerImpl server, String id) {
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.attached = new HashMap<>();
        this.trafficInfo = server.newTrafficInformation();
        this.events = new ArrayDeque<>();
        // make sure session is initially unblocked
    }



    public void setPending(boolean pending) {
        this.pending = pending;
    }



    public boolean isPending() {
        return this.pending;
    }



    void clearData() {
        synchronized (this.attached) {
            this.attached.clear();
        }
    }



    void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }



    @Override
    public Date getLastActionDate() {
        return this.lastAction;
    }



    /**
     * Adds an event to this session's event history. If the history grows
     * bigger than {@link #EVENTS_TO_BUFFER}, the eldest entry will be removed
     * from the history.
     * 
     * @param event
     *            Event to remember.
     */
    void addEvent(HttpEvent event) {
        synchronized (this.events) {
            this.events.add(event);
            if (this.events.size() == EVENTS_TO_BUFFER) {
                this.events.poll();
            }
        }
    }



    /**
     * Determiens whether this session is marked to be killed.
     * 
     * @return Whether this session should be killed upon next action
     */
    boolean shouldKill() {
        return this.doKill;
    }



    @Override
    public Deque<HttpEvent> getEvents() {
        return this.events;
    }



    @Override
    public String getId() {
        return this.id;
    }



    @Override
    public void setExpirationDate(Date d) {
        this.expirationDate = d;
    }



    @Override
    public Date getExpirationDate() {
        return this.expirationDate;
    }



    @Override
    public long getTimestamp() {
        return this.timestamp;
    }



    @Override
    public void set(String key, Object item) {
        synchronized (this.attached) {
            this.attached.put(key, item);
        }
    }



    @Override
    public void detach(String key) {
        synchronized (this.attached) {
            this.attached.remove(key);
        }
    }



    @Override
    public boolean isSet(String key) {
        synchronized (this.attached) {
            return this.attached.containsKey(key);
        }
    }



    @Override
    public Object get(String key) {
        synchronized (this.attached) {
            return this.attached.get(key);
        }
    }



    @Override
    public Map<String, Object> getAttached() {
        return Collections.unmodifiableMap(this.attached);
    }



    @Override
    public TrafficInformationImpl getTrafficInfo() {
        return this.trafficInfo;
    }



    @Override
    public void kill() {
        this.doKill = true;
    }
}
