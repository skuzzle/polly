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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;



/**
 * Provides static factory methods for some different types of {@link HttpAnswer}.
 * 
 * @author Simon Taddiken
 */
public final class HttpAnswers {

    
    private HttpAnswers() {}
    
    
    /**
     * Creates an answer which simply sends back the provided string to the client. After
     * calling this method, you may call <code>getCookies().add(...)</code> on the 
     * result to add some cookies that should be set at the client.
     * 
     * <p>The response code of this answer is 200</p>
     * 
     * @param message The string to send as response body.
     * @return An answer that sends back the specified string. 
     */
    public final static HttpAnswer newStringAnswer(final String message) {
        return newStringAnswer(200, message);
    }
    
    
    
    /**
     * Creates an answer which simply sends back the provided string to the client. After
     * calling this method, you may call <code>getCookies().add(...)</code> on the 
     * result to add some cookies that should be set at the client.
     * 
     * @param responseCode HTTP response code of this answer
     * @param message The string to send as response body.
     * @return An answer that sends back the specified string. 
     */
    public final static HttpAnswer newStringAnswer(int responseCode, 
            final String message) {
        return new HttpBinaryAnswer(responseCode) {
            @Override
            public void getAnswer(OutputStream out) throws IOException {
                final Writer w = new BufferedWriter(new OutputStreamWriter(out));
                w.write(message);
                w.flush();
            }
        };
    }
    
    
    
    @SuppressWarnings("unchecked")
    public final static HttpAnswer newTemplateAnswer(String relativeTemplatePath, 
            Object...context) {
        
        if (context.length == 1 && context[0] instanceof Map) {
            // little help for the compiler
            return newTemplateAnswer(relativeTemplatePath, 
                (Map<String, Object>)context[0]);
        }
        
        if (context.length % 2 != 0) {
            throw new IllegalArgumentException("length % 2 != 0");
        }
        final Map<String, Object> c = new HashMap<>(context.length);
        for (int i = 0; i < context.length; i += 2) {
            c.put((String) context[i], context[i + 1]);
        }
        
        return newTemplateAnswer(relativeTemplatePath, c);
    }



    public final static HttpAnswer newTemplateAnswer(
            final String relativeTemplatePath, 
            final Map<String, Object> context) {
        
        return newTemplateAnswer(200, relativeTemplatePath, context);
    }
    
    
    
    public final static HttpAnswer newTemplateAnswer(int responseCode,
        final String relativeTemplatePath, 
        final Map<String, Object> context) {
    
        return new HttpTemplateAnswer(responseCode) {
            
            @Override
            public String getRelativeTemplatePath() {
                return relativeTemplatePath;
            }
            
            
            
            @Override
            public void getAnswer(Map<String, Object> mappings) {
                mappings.putAll(context);
            }
        };
    }
}
