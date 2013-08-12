package de.skuzzle.polly.tools.streams;

import java.io.Writer;

/**
 * Just like the JDK's {@link StringWriter} this writer can be used to create Strings.
 * It uses a StringBuilder instead of a StringBuffer to create the String. Methods of
 * this writer never throw exceptions.
 * 
 * @author Simon Taddiken
 */
public class StringBuilderWriter extends Writer {

    private final StringBuilder stringBuilder;
    
    
    
    /**
     * Creates a new Writer thet writes to the given StringBuilder.
     * 
     * @param target The StringBuilder to write to.
     */
    public StringBuilderWriter(StringBuilder target) {
        this.stringBuilder = target;
    }
    
    
    
    /**
     * Creates a new Writer that writes to a newly created StringBuilder. It can be 
     * obtained using {@link #getBuilder()}.
     */
    public StringBuilderWriter() {
        this(new StringBuilder());
    }
    
    
    
    /**
     * Creates a new writer that writes to a newlys created StringBuilder. The 
     * StringBuilder is created with the given initial capacity.
     * 
     * @param initialCapacity Initial capacity of the StringBuilder to create.
     */
    public StringBuilderWriter(int initialCapacity) {
        this(new StringBuilder(initialCapacity));
    }
    
    
    
    /**
     * Gets the underlying StringBuilder to which this writer writes its data.
     * 
     * @return The StringBuilder.
     */
    public StringBuilder getBuilder() {
        return this.stringBuilder;
    }
    
    
    
    @Override
    public void close() {}

    @Override
    public void flush() {}

    
    
    @Override
    public void write(char[] cbuf, int off, int len) {
        this.stringBuilder.append(cbuf, off, len);
    }
}
