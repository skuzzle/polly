package de.skuzzle.polly.process;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simple {@link StreamHandler} implementation which reads all the data from an 
 * {@link InputStream} into a buffer and then discards it.
 * 
 * @author Simon Taddiken
 */
public class SilentStreamHandler extends StreamHandler {

    /**
     * Creates a new SilentStreamHandler with a default name.
     */
    public SilentStreamHandler() {
        super("SILENT");
    }
    
    
    
    /**
     * Creates a new SilentStreamHandler with the given name.
     * 
     * @param name Name of the thread in which this handler wil lbe executed.
     */
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
