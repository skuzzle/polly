package de.skuzzle.polly.core.parser;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

/**
 * <p>Reader implementation that allows to push back characters into the stream and 
 * provides position tracking. In order to be able to properly track positions of pushed 
 * back characters, this stream distinguishes between normal push backs and invisible 
 * push backs. Also note that this stream uses a FIFO buffer for pushed back
 * characters.</p>
 * 
 * <p>A normal push back may only be used if previously a character has been read from
 * the stream. This may be used to replace the next character with another or to simply
 * push back a character that was read by mistake. The following sample replaces the
 * next character with another one:</p>
 * <pre>
 * void replaceNextWith(PushbackReader reader, int c) {
 *     reader.read();
 *     reader.pushback(c);
 * </pre>
 * <p>The next call to read() yields the pushed back character <code>c</code>. The 
 * position of the stream is not modified, as for each character that was read another 
 * one was pushed back.</p>
 * 
 * <p>Invisible push backs may be used to insert characters that do not occur in the
 * original stream and should thus not modify the position of the stream.</p>
 * <pre>
 * void insert(PushbackReader reader, int c) {
 *     reader.pushbackInvisible(c);
 * }
 * </pre>
 * <p>The next call to read() yields the pushed back character, but as long as no further
 * characters are read from the original stream, this streams position will not change.
 * </p>
 * 
 * <p>This reader does not support mark()/reset().</p>
 *  
 * @author Simon Taddiken
 */
public class PushbackReader extends FilterReader {

    /** Byte code representing the EOS 'char' */
    public final static int EOS = -1;
    
    
    protected final static class Pushback {
        protected final int character;
        protected final boolean invisible;
        
        public Pushback(int character, boolean invisible) {
            super();
            this.character = character;
            this.invisible = invisible;
        }
    }
    
    
    
    private final LinkedList<Pushback> buffer;
    
    /** current position within the underlying stream */
    protected int position;
    
    /** whether all characters from the underlying stream have been read */
    protected boolean eos;
    
    /** Whether the last read character was invisible */
    protected boolean wasInvisible;
    
    
    
    public PushbackReader(Reader backend) {
        super(backend);
        this.buffer = new LinkedList<Pushback>();
    }

    
    
    /**
     * Determines whether there are more characters to read. This is the case if, and 
     * only if the underlying stream has no more characters to read and this stream's
     * push back buffer is empty.
     *  
     * @return Whether there are more characters to read.
     */
    public boolean eos() {
        return this.eos && this.buffer.isEmpty();
    }
    
    
    
    /**
     * <p>Gets the current index within the original input. Position will be updated by 
     * calls to {@link #read()} and push back actions.</p>
     * 
     * @return The current index within the input.
     */
    public int getPosition() {
        return this.position;
    }
    
    
    
    /**
     * Pushes back a character into this stream's FIFO buffer without modifying its 
     * current position.
     * 
     * @param c The character to push back.
     */
    public void pushbackInvisible(int c) {
        this.pushback(c, true);
    }
    
    
    
    /**
     * <p>Pushes back a character into this streams FIFO buffer and decreases the current 
     * stream position by 1. The actual stream position will never be below zero.</p>
     * 
     * <p>Whether the last read character was an invisible can be determined with
     * {@link #wasInvisible()}.</p>
     * 
     * @param c The character to push back.
     */
    public void pushback(int c) {
        this.pushback(c, false);
    }
    
    
    
    /**
     * Whether the last call to {@link #read()} yielded an invisible character.
     * @return Whether the last call to {@link #read()} yielded an invisible character.
     */
    public boolean wasInvisible() {
        return this.wasInvisible;
    }
    
    
    
    private void pushback(int c, boolean invisible) {
        invisible |= c == EOS;
        this.buffer.add(new Pushback(c, invisible));
        this.position = invisible ? 
            this.position : 
            Math.max(this.position - 1, 0);
    }

    
    

    /**
     * Reads the next character. If the push back buffer is empty, a character is read
     * from the underlying reader and position of this stream is increased by 1.
     * If there is a character in the push back buffer, it is removed. Position will only 
     * be increased if that character was not pushed invisible.
     *  
     * @return The next character.
     * @throws IOException If an I/O error occurred.
     */
    public int read() throws IOException {
        if (this.buffer.isEmpty()) {
            final int next = super.read();
            this.eos = next == -1;
            this.position += this.eos ? 0 : 1;
            this.wasInvisible = false;
            return next;
        } else {
            final Pushback pb = this.buffer.poll();
            this.position += pb.invisible ? 0 : 1;
            this.wasInvisible = pb.invisible;
            return pb.character;
        }
    }
    
    

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (len <= 0) {
            if (len < 0 || off < 0 || off > cbuf.length) {
                throw new IndexOutOfBoundsException();
            }
            // len is 0
            return 0;
        }
        
        // first, read characters from buffer
        int avail = this.buffer.size();
        if (avail > 0) {
            if (len < avail) {
                avail = len;
            }
            for (int i = 0; i < avail; ++i) {
                cbuf[off + i] = (char) this.read();
            }
            off += avail;
            len -= avail;
        }
        
        // read characters from stream
        if (len > 0) {
            len = super.read(cbuf, off, len);
            if (len == -1) {
                return avail == 0 ? - 1 : avail;
            }
            return avail + len;
        }
        return avail;
    }

    
    
    @Override
    public void close() throws IOException {
        this.buffer.clear();
        super.close();
    }
    
    
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    
    
    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw new IOException("mark not supported");
    }
    
    
    
    @Override
    public void reset() throws IOException {
        throw new IOException("reset not supported");
    }
}
