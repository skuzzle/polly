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
package de.skuzzle.polly.http.internal;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


class CountingOutputStream extends FilterOutputStream {

    private final TrafficInformationImpl trafficInfo;
    
    public CountingOutputStream(OutputStream out, TrafficInformationImpl trafficInfo) {
        super(out);
        this.trafficInfo = trafficInfo;
    }

    
    
    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    
    
    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        this.trafficInfo.updateUpload(len);
        this.out.write(b, off, len);
    }
    
    
    
    @Override
    public synchronized void write(int b) throws IOException {
        this.trafficInfo.updateUpload(1);
        this.out.write(b);
    }
}
