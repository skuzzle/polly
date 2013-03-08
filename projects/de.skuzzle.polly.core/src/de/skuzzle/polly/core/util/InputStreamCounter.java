package de.skuzzle.polly.core.util;

import java.io.IOException;
import java.io.InputStream;


public class InputStreamCounter extends InputStream {

    private int bytes;
    private InputStream backend;
    
    
    public InputStreamCounter(InputStream backend) {
        this.backend = backend;
    }
    
    
    
    public int getBytes() {
        return this.bytes;
    }
    
    
    
    @Override
    public int read() throws IOException {
        ++this.bytes;
        return this.backend.read();
    }

}
