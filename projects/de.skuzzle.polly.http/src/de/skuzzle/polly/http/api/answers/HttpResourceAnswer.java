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
import java.io.InputStream;
import java.io.OutputStream;

import de.skuzzle.polly.http.api.HttpServer;


public class HttpResourceAnswer extends HttpBinaryAnswer {

    private static final int BUFFER_SIZE = 1024;
    
    private final String resourcePath;
    private final ClassLoader cl;
    
    
    public HttpResourceAnswer(int responseCode, ClassLoader cl, String resourcePath) {
        super(responseCode);
        this.resourcePath = resourcePath;
        this.cl = cl;
    }

    
    
    @Override
    public void getAnswer(OutputStream out, HttpServer server) throws IOException {
        InputStream in = null;
        try {
            final String path = this.resourcePath.startsWith("/") ?
                this.resourcePath.substring(1) : this.resourcePath;
            in = this.cl.getResourceAsStream(path);
            
            if (in == null) {
                return;
            }
            final byte[] buffer = new byte[BUFFER_SIZE];
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
}
