package de.skuzzle.polly.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;


/**
 * This {@link StreamHandler} allows to asynchronously redirect an InputStream to
 * an {@link OutputStream}.
 * 
 * @author Simon
  */
public class RedirectStreamHandler extends StreamHandler {

    private OutputStream output;
    
    
    
    /**
     * Creates a RedirectStreamHandler which redirects its input to System.out.
     * The stream to redirect can be set using {@link #setStream(InputStream)}.
     * 
     * @param name The name of the thread used for stream handling.
     */
    public RedirectStreamHandler(String name) {
        super(name);
        this.output = System.out;
    }
    
    
    
    /**
     * Creates a RedirectStreamHandler which redirects its input to System.out using
     * a default name.
     * 
     * @param stream The stream to handle. Note: When using a StreamHandler with a 
     *      {@link ProcessExecutor}, this InputStream will be overridden with the 
     *      InputStream of the created process.
     */
    public RedirectStreamHandler(InputStream stream) {
        super(stream);
        this.output = System.out;
    }
    
    
    
    /**
     * Creates a RedirectStreamHandler which redirects its input to a certain 
     * {@link OutputStream}.
     * 
     * @param stream The stream to handle. Note: When using a StreamHandler with a 
     *      {@link ProcessExecutor}, this InputStream will be overridden with the 
     *      InputStream of the created process.
     * @param output The stream to output the incoming data.
     * @param name The name of the thread used for stream handling.
     */
    public RedirectStreamHandler(InputStream stream, OutputStream output, String name) {
        super(stream, name);
        this.output = output;
    }
    
    
    
    /**
     * Creates a RedirectStreamHandler which redirects its input to a certain 
     * {@link OutputStream} using a default name.
     * 
     * @param stream The stream to handle. Note: When using a StreamHandler with a 
     *      {@link ProcessExecutor}, this InputStream will be overridden with the 
     *      InputStream of the created process.
     * @param output The stream to output the incoming data.
     */
    public RedirectStreamHandler(InputStream stream, OutputStream output) {
        super(stream);
        this.output = output;
    }
    

    
    @Override
    protected void handle(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        PrintWriter writer = new PrintWriter(this.output);
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                writer.println(line);
                writer.flush();
            }
        } finally {
            writer.close();
        }
    }
}
