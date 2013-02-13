package de.skuzzle.polly.tools.streams;

import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Convenience class that exists to only spare one additional constructor call when
 * needing a PrintStream that writes into a {@link StringBuilder}. This class only
 * calls the constructor of its super class with a newly created 
 * {@link StringBuilderOutputStream} instance.
 * 
 * @author Simon Taddiken
 * @deprecated see {@link StringBuilderOutputStream}.
 */
public class StringBuilderPrintStream extends PrintStream {

    
    
    /**
     * Creates a new Stream which writes to the given target StringBuilder.
     * 
     * @param target The StringBuilder to write to.
     */
    public StringBuilderPrintStream(StringBuilder target) {
        this(target, Charset.defaultCharset());
    }
    
    
    
    /**
     * Creates a new Stream which writes to the given target StringBuilder using the
     * {@link Charset} with the specified name.
     * 
     * @param target The StringBuilder to write to.
     * @param cs Name of the Charset to decode the bytes to write with.
     */
    public StringBuilderPrintStream(StringBuilder target, String cs) {
        this(target, Charset.forName(cs));
    }
    
    
    
    /**
     * Creates a new Stream which writes to the given target StringBuilder using the
     * specified {@link Charset}.
     * 
     * @param target The StringBuilder to write to.
     * @param cs The Charset to decode the bytes to write with.
     */
    public StringBuilderPrintStream(StringBuilder target, Charset cs) {
        super(new StringBuilderOutputStream(target, cs));
    }
}