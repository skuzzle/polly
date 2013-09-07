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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.skuzzle.polly.http.api.ResolvedFile;


/**
 * Answer used to send a file back to the client. This answer can be handled natively by 
 * the server and does not need a specific answer handler registered as it can be handled
 * as any binary answer.
 * 
 * <p>For safety reasons, this answer only supports absolute files which were resolved 
 * to a certain root directory before being sent. Such a {@link ResolvedFile} can be 
 * obtained by a {@link de.skuzzle.polly.http.api.FileResolver FileResolver} instance.
 * 
 * @author Simon Taddiken
 */
public class HttpFileAnswer extends HttpBinaryAnswer {

    /** Buffer size to use when reading the file to send */
    public final static int BUFFER_SIZE = 1024;
    
    /** The file that will be sent */
    private final File dest;
    
    
    
    public HttpFileAnswer(ResolvedFile file) {
        this(200, file);
    }
    
    
    
    public HttpFileAnswer(int responseCode, ResolvedFile file) {
        super(responseCode);
        this.dest = file.getFile();
    }

    
    
    @Override
    public void getAnswer(OutputStream out) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(this.dest);
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
