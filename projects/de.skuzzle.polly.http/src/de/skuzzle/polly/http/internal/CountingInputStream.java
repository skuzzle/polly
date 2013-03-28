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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.skuzzle.polly.http.api.TrafficInformation;

/**
 * {@link InputStream} wrapper which counts all read bytes and updates a 
 * {@link TrafficInformation} instance.
 * 
 * @author Simon Taddiken
 */
class CountingInputStream extends FilterInputStream {

    /** The updated TrafficInformation */
    protected final TrafficInformationImpl trafficInfo;
    
    /**
     * Creates a new {@link CountingInputStream}.
     * 
     * @param in The wrapped InputStream.
     * @param trafficInfo TrafficInformation that are updated when data is read.
     */
    public CountingInputStream(InputStream in, TrafficInformationImpl trafficInfo) {
        super(in);
        this.trafficInfo = trafficInfo;
    }
    
    
    
    @Override
    public synchronized int read() throws IOException {
        this.trafficInfo.updateDownload(1);
        return this.in.read();
    }
    
    
    
    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    
    
    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        final int read = super.read(b, off, len);
        this.trafficInfo.updateDownload(len);
        return read;
    }
}
