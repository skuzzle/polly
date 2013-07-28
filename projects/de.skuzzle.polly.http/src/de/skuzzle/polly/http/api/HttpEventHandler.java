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
 * HttpEventHandlers need to be registered with a {@link HttpServer} and may
 * be used to respond to certain {@link HttpEvent HttpEvents}.
 * 
 * <p>Once registered, the handler will be notified about an HttpEvent and
 * may create a {@link HttpAnswer} to reply to it. If your handler decides not to
 * handle that event, it can simply pass it to the next registered handler.</p>
 * 
 * @author Simon Taddiken
 */
public interface HttpEventHandler {
    
    /**
     * Tries to handle the provided {@link HttpEvent}. If the event can be handled, this
     * method must return a proper {@link HttpAnswer} which defines how data is sent back
     * to the client. If this handler can not handle the event, you should return the
     * result of the next handler by calling <code>next.handleHttpEvent(e, next)</code>.
     * However if you do not want the other handlers to be notified about this event, 
     * you may simply return <code>null</code> to abort event handling right now.
     * 
     * <p>For example:</p>
     * <pre>
     * public void handleEvent(HttpEvent e, HttpEventHandler next) throws HttpException {
     *     if (e.get("foo") != null) {
     *         // handle the event by creating an answer right here
     *         return HttpAnswers.createStringAnswer("bar");
     *     } else if (e.post("bar") != null) {
     *         // 'consume' this event, no other handlers are notified about it
     *         return null;
     *     } else {
     *         // use the next handler to handle this event
     *         return next.handleEvent(e, next);
     *     }
     * }
     * </pre>
     * 
     * <p><code>HttpAnswers</code> can easily be created using the {@link HttpAnswers}
     * utility class.</p>
     * 
     * @param e The HttpEvent to handle.
     * @param next The next handler in the servers handler chain. Each invocation of 
     *          its <code>handleEvent</code> method will be delegated to the next handler
     *          in the chain until all have been notified. In that case, it will return
     *          <code>null</code>.
     * @return The HttpAnswer as response to the provided event, or <code>null</code> to
     *          abort handling of this event.
     * @throws HttpException If an error occurred upon handling the event. In this case
     *          the server will try the next registered handler.
     */
    public HttpAnswer handleHttpEvent(HttpEvent e, HttpEventHandler next) 
        throws HttpException;
}
