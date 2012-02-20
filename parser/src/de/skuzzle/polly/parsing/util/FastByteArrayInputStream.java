package de.skuzzle.polly.parsing.util;

import java.io.InputStream;


public class FastByteArrayInputStream extends InputStream {

    private byte[] buffer;
    
    private int pos;
    
    private int size;
    
    
    
    public FastByteArrayInputStream(FastByteArrayOutputStream out) {
        this(out.getBuffer(), out.getBufferSize());
    }
    
    
    
    public FastByteArrayInputStream(byte[] buffer, int size) {
        this.size = size;
        this.buffer = buffer;
        this.pos = 0;
    }
    
    
    
    @Override
    public int read() {
        return this.pos < this.size ? this.buffer[this.pos++] : -1;
    }
    
    
    
    @Override
    public int read(byte[] b, int off, int len) {
        if (this.pos >= this.size) {
            return -1;
        }
        if (this.pos + len > this.size) {
            len = this.size - this.pos;
        }
        System.arraycopy(this.buffer, this.pos, b, off, len);
        this.pos += len;
        return len;
    }
    
    
    
    public long skip(long n) {
        if (this.pos + n > this.size) {
            n = this.size - this.pos;
        }
        if (n < 0) {
            return 0;
        }
        this.pos += n;
        return n;
    };
}