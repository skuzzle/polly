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

import java.io.IOException;
import java.io.OutputStream;

import de.skuzzle.polly.http.api.HttpEvent;


/**
 * If you implement custom {@link HttpAnswer HttpAnswers}, you must also provide
 * a suitable HttpAnswerHandler which is able to handle that new type of answers you
 * created.
 * 
 * <p>Instances of this class are responsible for actually sending data back to the client
 * if a {@link HttpEventHandler} successfully handled an event.</p>
 * 
 * <p>When a HttpEventHandler returned a {@link HttpAnswer} instance, the 
 * {@link HttpServer} will try to resolve a suitable handler depending on the runtime 
 * type information of that answer. See {@link HttpServer#getHandler(HttpAnswer)} for
 * details on how a handler is resolved.</p>
 * 
 * <p>See {@link HttpAnswer} for a concrete implementation example of custom answers and
 * answer handlers.</p>
 * 
 * @author Simon Taddiken
 * @see HttpAnswer
 */
public abstract class HttpAnswerHandler {

    public abstract void handleAnswer(HttpAnswer answer, HttpEvent e, OutputStream out) 
        throws IOException;
}