package de.skuzzle.polly.tools.streams;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

// TODO: 
/**
 * Convenience {@link OutputStream} implementation to write into {@link StringBuilder}s. 
 * This can be used for example as bridge between {@link PrintStream}s and
 * StringBuilders.
 *  
 * @author Simon Taddiken
 * @deprecated This class does not work correctly by now
 */
public class StringBuilderOutputStream extends OutputStream {
    
    /**
     * Bytes to buffer before the bytes are actually decoded into characters and put
     * into the StringBuilder.
     */
    protected final static int BUFFER_SIZE = 10;
    
    protected final StringBuilder stringBuilder;
    protected final CharsetDecoder decoder;
    protected final ByteBuffer buffer;
    
    
    
    /**
     * Creates a new Stream which writes to the given target StringBuilder.
     * 
     * @param target The StringBuilder to write to.
     */
    public StringBuilderOutputStream(StringBuilder target) {
        this(target, Charset.defaultCharset());
    }
    
    
    
    /**
     * Creates a new Stream which writes to the given target StringBuilder using the
     * {@link Charset} with the specified name.
     * 
     * @param target The StringBuilder to write to.
     * @param cs Name of the Charset to decode the bytes to write with.
     */
    public StringBuilderOutputStream(StringBuilder target, String cs) {
        this(target, Charset.forName(cs));
    }
    
    
    
    /**
     * Creates a new Stream which writes to the given target StringBuilder using the
     * specified {@link Charset}.
     * 
     * @param target The StringBuilder to write to.
     * @param cs The Charset to decode the bytes to write with.
     */
    public StringBuilderOutputStream(StringBuilder target, Charset cs) {
        this.decoder = cs.newDecoder();
        this.stringBuilder = target;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.buffer.mark();
        this.buffer.reset();
    }
    
    
    
    @Override
    public void write(int b) throws CharacterCodingException {
        this.buffer.put((byte)(b & 0xFF));
        if (!this.buffer.hasRemaining()) {
            this.flush();
        }
    }
    
    
    
    @Override
    public void close() throws CharacterCodingException {
        this.flush();
    }
    
    
    
    @Override
    public void flush() throws CharacterCodingException {
        if (this.buffer.position() == 0) {
            return;
        }
        this.buffer.reset();
        CharBuffer buff = this.decoder.decode(this.buffer);
        this.buffer.reset();
        this.stringBuilder.append(buff);
    }
}