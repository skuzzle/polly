package de.skuzzle.polly.process;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class SilentStreamHandler extends StreamHandler {

    public SilentStreamHandler() {
        super("SILENT");
    }
    
    
    
    public SilentStreamHandler(String name) {
        super(name);
    }

    
    
    @Override
    protected void handle(InputStream stream) throws Exception {
        Reader reader = new InputStreamReader(stream);
        char[] buffer = new char[1024];
        while (reader.read(buffer) > 0) {
            if (this.isInterrupted()) { throw new InterruptedException(); }
        }
    }
}
