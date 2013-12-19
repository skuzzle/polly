package de.skuzzle.polly.tools.io;

import java.io.Closeable;



public interface AllocationStrategy extends Closeable {
    
    /**
     * Records the given object as a consumer which will allocate bytes until 
     * {@link #consumerFinished(Object)} with the same object has been called.
     * 
     * @param obj The object to register as consumer.
     */
    public void registerConsumer(Object obj);
    
    /**
     * Records that the given consumer is done allocating bytes with this strategy.
     * @param obj The consumer object which finished allocating.
     */
    public void consumerFinished(Object obj);
    
    /**
     * Gets the speed in bytes per seconds at which this allocator currently allocates.
     * 
     * @return The current allocation speed in bytes per second
     */
    public double getSpeed();
    
    /**
     * Tries to allocate the given number of bytes. This method does not actually allocate
     * any memory, but figures out how many memory there is available to the caller. 
     * The result must always be in the interval of <tt>[0, bytes]</tt>. Using the 
     * <tt>source</tt> parameter, implementors are able to distinguish different callers 
     * and my assign different priorities.
     * 
     * <p>This method may or may not block if no bytes are available. Implementors should
     * document the behavior regarding this issue.</p>
     * 
     * @param source The caller object
     * @param bytes The number of bytes to allocate.
     * @return A number between <tt>0</tt> and <tt>bytes</tt>, both inclusive indices.
     */
    public int allocate(Object source, int bytes);
}
