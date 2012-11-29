package de.skuzzle.polly.tools.streams;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>{@link OutputStream} implementation that writes into a byte array. It differs from 
 * the implementation provided by the JDK in that this stream is not synchronized and
 * thus spares out some runtime overhead. Additionally, the internal buffer is never
 * copied when being returned by {@link #getBuffer()} making this class more suitable
 * for certain operations.</p>
 * 
 * <p>None of the IO methods in this stream will throw an {@link IOException}.</p>
 * 
 * @author Simon Taddiken
 */
public class FastByteArrayOutputStream extends OutputStream {

    /** Default buffer size */
    protected final static int BUFFER_SIZE = 1024;
    
    protected byte[] buffer;
    protected int size;
    
    
    
    /**
     * Creates a new FastByteArrayOutputStream with the default buffer size (1024). The 
     * buffer will grow automatically when its initial capacity is exceeded.
     */
    public FastByteArrayOutputStream() {
        this.buffer = new byte[BUFFER_SIZE];
    }
    
    
    
    /**
     * Creates a new FastByteArrayOutputStream with the given buffer size. The 
     * buffer will grow automatically when its initial capacity is exceeded.
     * 
     * @param bufferSize Initial buffer capacity.
     */
    public FastByteArrayOutputStream(int bufferSize) {
        this.buffer = new byte[bufferSize];
    }
    
    
    
    /**
     * Gets the internal buffer to which data has been written without copying it. It 
     * contains {@link #getBufferSize()} bytes.
     * 
     * @return The internal byte buffer.
     */
    public byte[] getBuffer() {
        return this.buffer;
    }
    
    
    
    /**
     * Amount of data that has been written to the internal buffer. This can be reset
     * using {@link #reset()} so the buffer can be reused by this stream for a new 
     * unrelated write operation.
     * 
     * @return The internal buffer size.
     */
    public int getBufferSize() {
        return this.size;
    }
    
    
    
    private void checkBuffer(int newSize) {
        newSize = newSize + this.size;
        if (newSize > this.buffer.length) {
            byte[] tmp = this.buffer;
            this.buffer = new byte[Math.max(newSize, this.buffer.length * 2)];
            System.arraycopy(tmp, 0, this.buffer, 0, tmp.length);
            tmp = null;
        }
    }
    
    
    
    /**
     * Resets this stream so its buffer can be reused for another unrelated output 
     * operation. 
     */
    public void reset() {
        this.size = 0;
    }
    
    
    
    @Override
    public void write(byte[] b) {
        this.write(b, 0, b.length);
    }
    
    
    
    @Override
    public void write(byte[] b, int off, int len) {
        this.checkBuffer(len);
        System.arraycopy(b, off, this.buffer, this.size, len);
        this.size += len;
    }
    
    
    
    @Override
    public void write(int b) {
        this.checkBuffer(1);
        this.buffer[this.size++] = (byte) b; 
    }
    
    
    
    @Override
    public void close() {
        this.buffer = null;
    }
}
