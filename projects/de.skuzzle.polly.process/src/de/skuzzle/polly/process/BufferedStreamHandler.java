package de.skuzzle.polly.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class BufferedStreamHandler extends StreamHandler {

    private StringBuilder buffer;
    
    public BufferedStreamHandler(String name) {
        super(name);
        this.buffer = new StringBuilder();
    }
    
    
    
    public BufferedStreamHandler(InputStream stream, String name) {
        super(stream, name);
        this.buffer = new StringBuilder();
    }
    

    
    public BufferedStreamHandler(InputStream stream) {
        super(stream);
        this.buffer = new StringBuilder();
    }
    
    
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    
    
    @Override
    protected void handle(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            this.buffer.append(line);
            this.buffer.append("\n");
        }
    }
    
}

