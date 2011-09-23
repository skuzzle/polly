package de.skuzzle.polly.installer;

import java.io.IOException;
import java.io.OutputStream;


public class SilentStream extends OutputStream {

    public SilentStream() {
        super();
    }
    
    
    @Override
    public void write(byte[] b) throws IOException {}
    
    
    @Override
    public void write(byte[] buf, int off, int len) {}


    @Override
    public void write(int b) throws IOException {}

}
