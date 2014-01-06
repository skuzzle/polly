package de.skuzzle.polly.tools.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FastByteArrayOutputStream extends OutputStream {

    private final static int BUFFER_SIZE = 1024 * 5;
    
    private byte[] buffer;
    private int size;
    
    
    public FastByteArrayOutputStream() {
        this.buffer = new byte[BUFFER_SIZE];
    }
    
    
    
    public FastByteArrayOutputStream copy() {
        final FastByteArrayOutputStream result = new FastByteArrayOutputStream(this.size);
        System.arraycopy(this.buffer, 0, result.buffer, 0, this.size);
        return result;
    }
    
    
    
    public FastByteArrayOutputStream(int bufferSize) {
        this.buffer = new byte[bufferSize];
    }
    
    
    
    public InputStream getInputStreamForBuffer() {
        return new FastByteArrayInputStream(this);
    }
    
    
    
    
    public byte[] getBuffer() {
        return this.buffer;
    }
    
    
    
    public int getBufferSize() {
        return this.size;
    }
    
    
    
    public void shrink() {
        final byte[] target = new byte[this.size];
        System.arraycopy(this.buffer, 0, target, 0, this.size);
        this.buffer = target;
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