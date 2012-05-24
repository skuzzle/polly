package de.skuzzle.polly.parsing.util;

import java.io.IOException;
import java.io.OutputStream;


public class FastByteArrayOutputStream extends OutputStream {

    private final static int BUFFER_SIZE = 1024 * 5;
    
    private byte[] buffer;
    private int size;
    
    
    public FastByteArrayOutputStream() {
        this.buffer = new byte[BUFFER_SIZE];
    }
    
    
    
    
    public FastByteArrayOutputStream(int bufferSize) {
        this.buffer = new byte[bufferSize];
    }
    
    
    
    
    public byte[] getBuffer() {
        return this.buffer;
    }
    
    
    
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
    
    
    
    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    
    
    @Override
    public void write(byte[] b, int off, int len) {
        this.checkBuffer(len);
        System.arraycopy(b, off, this.buffer, this.size, len);
        this.size += len;
    }
    
    
    
    @Override
    public void write(int b) throws IOException {
        this.buffer[this.size++] = (byte) b; 
    }
    
    
    
    @Override
    public void close() {
        this.buffer = null;
    }

}
