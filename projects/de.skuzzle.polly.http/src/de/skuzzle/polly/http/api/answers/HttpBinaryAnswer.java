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

import de.skuzzle.polly.http.api.HttpServer;

/**
 * <p>Most basic {@link HttpAnswer} subtype which is able to send binary data back to the
 * client. Instances of this type can automatically be handled by the server, so that
 * there is no need to provide your own {@link HttpHandler} if you implement this 
 * interface (unless you need special treatment for your implementation).</p>
 * 
 * <p>You can obtain concrete instances of this interface using {@link HttpAnswers}.</p>
 * 
 * @author Simon Taddiken
 */
public abstract class HttpBinaryAnswer extends AbstractHttpAnswer {

    public HttpBinaryAnswer(int responseCode) {
        super(responseCode);
    }

    
    
    /**
     * Sends binary data back to the client.
     * 
     * @param server The server which processes the answer
     * @param out Stream to write into the response body to send data to the client.
     * @throws IOException If writing into the stream fails.
     */
    public abstract void getAnswer(OutputStream out, HttpServer server) throws IOException;
}