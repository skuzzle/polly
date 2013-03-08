package de.skuzzle.polly.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * {@link StreamHandler} implementation that stores all read data in a 
 * {@link StringBuilder} which can be retrieved using {@link #getBuffer()}.
 * 
 * @author Simon Taddiken
 */
public class BufferedStreamHandler extends StreamHandler {

    private StringBuilder buffer;
    
    /**
     * Creates a new BufferedStreamHandler with the given name.
     * 
     * @param name Name of the thread for this StreamHandler.
     */
    public BufferedStreamHandler(String name) {
        super(name);
        this.buffer = new StringBuilder();
    }
    
    
    
    /**
     * Creates a new BufferedStreamHandler for the given InputStream and name.
     * 
     * @param stream The {@link InputStream} from which this StreamHandler reads data.
     * @param name Name of the thread for this StreamHandler.
     */
    public BufferedStreamHandler(InputStream stream, String name) {
        super(stream, name);
        this.buffer = new StringBuilder();
    }
    

    
    /**
     * Creates a new BufferedStreamHandler for the given InputStream using a generated
     * default thread name.
     * 
     * @param stream The {@link InputStream} from which this StreamHandler reads data.
     */
    public BufferedStreamHandler(InputStream stream) {
        super(stream);
        this.buffer = new StringBuilder();
    }
    
    
    
    /**
     * Gets the buffer that contains the so far read data.
     * 
     * @return Tha buffer used by this StreamHandler.
     */
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