package de.skuzzle.polly.tools.streams;

import java.io.Closeable;



public interface AllocationStrategy extends Closeable {

    /**
     * Tries to allocate the given number of bytes. The result must always be in the
     * interval of <tt>[0, bytes]</tt>
     * 
     * @param bytes The number of bytes to allocate.
     * @return Actual number of bytes allocated.
     */
    public int allocate(int bytes);
}
