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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpCookie;

/**
 * <p>A HttpAnswer defines the result of a handled {@link HttpEvent} and must be returned
 * by a {@link HttpEventHandler} if it successfully handled the event. You probably do 
 * not want to implement this interface but use one of the provided default 
 * implementations (see below).</p>
 * 
 * <p>This interface only defines which cookies will be set and which is the http 
 * response code of the result. Actual handling of sending data back to the 
 * clients must be achieved by implementing this interface and then providing a
 * {@link HttpAnswerHandler} to the server.</p>
 * 
 * <p>Two general implementations of this interface and corresponding handlers already 
 * exist:
 * <ul>
 * <li>{@link HttpBinaryAnswer} - Will simply send binary data back to the client.</li>
 * <li>{@link HttpTemplateAnswer} - Will render a velocity template and send its data 
 *          back to the client.</li>
 * </ul>
 * </p>
 * 
 * <p>The utility class {@link HttpAnswers} provides static factory methods for 
 * pre-implemented HttpAnswer instances so that you rarely need to implement this
 * interface yourself.</p>
 * 
 * <p>Here follows an example of a custom http answer and corresponding handler which
 * simply sends back a string to the client.</p>
 * <pre>
 * public class SimpleStringAnswer implements HttpAnswer {
 * 
 *     private String answer;
 *     
 *     public SimpleStringAnswer(String answer) {
 *         this.answer = answer;
 *     }
 *     
 *     public int getResponseCode() { return 200; }
 *     
 *     public Collection&lt;HttpCookies&gt; getCookies() { return new ArrayList&lt;&gt;(); }
 *     
 *     public void handleAnswer(OutputStream out) throws IOException {
 *         Writer w = new OutputStreamWriter(out);
 *         w.write(this.answer);
 *         // closing not required
 *     }
 * }
 * </pre>
 * 
 * <p>Now follows a {@link HttpAnswerHandler} for this new type of HttpAnswer:</p>
 * <pre>
 * public class StringAnswerHandler implements HttpAnswerHandler {
 *     public void handleAnswer(HttpAnswer answer, HttpEvent e, OutputStream out) 
 *             throws IOException {
 *     
 *         SimpleStringAnswer ssa = (SimpleStringAnswer) answer;
 *         // call our custom method
 *         ssa.handleAnswer(out);
 *     }
 * }
 * </pre>
 * <p>This handler must now be registered with the {@link HttpServer} so that it actually
 * can handle the new type of answers:</p>
 * <pre>server.registerHandler(SimpleStringAnswer.class, new StringAnswerHandler());</pre>
 * 
 * @author Simon Taddiken
 * @see HttpAnswerHandler
 */
public abstract class HttpAnswer {
    
    /**
     * Gets the http response code of this answer.
     * 
     * @return The response code.
     */
    public abstract int getResponseCode();
    
    /**
     * Gets the response header map.
     * 
     * @return The response headers to be sent to the client.
     */
    public abstract Map<String, List<String>> getResponseHeaders();
    
    /**
     * Gets a collection of {@link HttpCookie cookies} which will be set on the client
     * side upon handling this answer.
     * 
     * @return Collection of cookies to set at the client.
     */
    public abstract Collection<HttpCookie> getCookies();
    
    public abstract HttpAnswer redirect(String url);
    
    public abstract HttpAnswer addHeader(String name, String value);

    public abstract HttpAnswer addCookie(HttpCookie cookie);
    
    public abstract HttpAnswer addCookie(String name, String value, int maxAge);
}