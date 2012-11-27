package de.skuzzle.polly.tools.strings;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Convenience {@link OutputStream} implementation to write into {@link StringBuilder}s. 
 * This can be used for example as bridge between {@link PrintStream}s and
 * StringBuilders.
 *  
 * @author Simon Taddiken
 */
public class StringBuilderOutputStream extends OutputStream {
    
    private final StringBuilder stringBuilder;
    
    
    /**
     * Creates a new Stream which writes to the given target StringBuilder.
     * 
     * @param target The StringBuilder to write to.
     */
    public StringBuilderOutputStream(StringBuilder target) {
        this.stringBuilder = target;
    }
    
    
    
    /**
     * Creates a new Stream which uses a newly created StringBuilder to write to. It can 
     * be retrieved using {@link #getStringBuilder()} method.
     */
    public StringBuilderOutputStream() {
        this.stringBuilder = new StringBuilder();
    }
    
    
    
    /**
     * Gets the underlying StringBuilder to which this Stream writes its data.
     * @return The StringBuilder.
     */
    public StringBuilder getStringBuilder() {
        return this.stringBuilder;
    }

    
    
    @Override
    public void write(int b) {
        this.stringBuilder.appendCodePoint(b);
    }
    
    
    
    @Override
    public void close() {}
    
    
    
    @Override
    public void flush() {}
}