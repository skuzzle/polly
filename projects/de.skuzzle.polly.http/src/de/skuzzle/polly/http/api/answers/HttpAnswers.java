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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpServer;


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
     * @param message The string to send as response body.
     * @return An answer that sends back the specified string. 
     */
    public final static HttpAnswer createStringAnswer(final String message) {
        return new HttpBinaryAnswer(200) {
            @Override
            public void getAnswer(OutputStream out) throws IOException {
                final Writer w = new BufferedWriter(new OutputStreamWriter(out));
                w.write(message);
                w.flush();
            }
        };
    }
    
    
    
    /**
     * Creates an answer which sends back the specified relative file to the client. Note
     * that the specified path must be relative to any of the server's web root 
     * directories, otherwise this method will throw a {@link FileNotFoundException}.
     * After calling this method, you may call <code>getCookies().add(...)</code> on the 
     * result to add some cookies that should be set at the client.
     * 
     * @param relativePath Relative path to the file which is to be sent.
     * @param server HttpServer instance which is used to resolve the relative file.
     * @return An answer instance for sending a file.
     * @throws FileNotFoundException If the file could not be resolved using
     *          {@link HttpServer#resolveRelativeFile(String)}.
     */
    public final static HttpAnswer createFileAnswer(final String relativePath, 
            HttpServer server) throws FileNotFoundException {
    
        // check whether file exists
        final File dest = server.resolveRelativeFile(relativePath);
        
        return new HttpBinaryAnswer(200) {
            @Override
            public void getAnswer(OutputStream out) throws IOException {
                InputStream in = null;
                try {
                    in = new FileInputStream(dest);
                    final byte[] buffer = new byte[1024];
                    int read = in.read(buffer, 0, buffer.length);
                    
                    while (read != -1) {
                        out.write(buffer, 0, read);
                        read = in.read(buffer, 0, buffer.length);
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        };
    }
    
    
    
    public final static HttpAnswer createTemplateAnswer(String relativeTemplatePath, 
            Object...context) {
        
        if (context.length % 2 != 0) {
            throw new IllegalArgumentException("length % 2 != 0");
        }
        final Map<String, Object> c = new HashMap<>(context.length);
        for (int i = 0; i < context.length; i += 2) {
            c.put((String) context[i], context[i + 1]);
        }
        
        return createTemplateAnswer(relativeTemplatePath, c);
    }



    public final static HttpAnswer createTemplateAnswer(
            final String relativeTemplatePath, 
            final Map<String, Object> context) {
        
        return new HttpTemplateAnswer(200) {
            
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
