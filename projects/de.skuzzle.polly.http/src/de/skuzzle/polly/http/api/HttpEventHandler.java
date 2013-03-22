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
import de.skuzzle.polly.http.api.answers.HttpAnswers;

/**
 * HttpEventHandlers need to be registered with a {@link HttpServer} and may
 * be used to respond to certain {@link HttpEvent HttpEvents}.
 * 
 * <p>Once registered, the handler will be notified about an HttpEvent and
 * may create a {@link HttpAnswer} to reply to it. However, the event handlers
 * are chained in a way that after the first registered handler created a suitable
 * answer, all further handlers are not notified about that particular event.</p>
 * 
 * @author Simon Taddiken
 */
public interface HttpEventHandler {
    
    /**
     * Tries to handle the provided {@link HttpEvent}. If the event can be handled, this
     * method must return a proper {@link HttpAnswer} which defines how data is sent back
     * to the client. If this handler can not handle the event, you may return 
     * <code>null</code> or throw a {@link HttpException}. If you do so, the 
     * {@link HttpServer} asks the next registered handler to handle the event.
     * 
     * <p><code>HttpAnswers</code> can easily be created using the {@link HttpAnswers}
     * utility class.</p>
     * 
     * @param e The HttpEvent to handle.
     * @return The HttpAnswer as response to the provided event, or <code>null</code> if
     *          further event handlers should be notified about the event.
     * @throws HttpException If an error occurred upon handling the event. In this case
     *          the server will try the next registered handler.
     */
    public HttpAnswer handleHttpEvent(HttpEvent e) throws HttpException;
}
