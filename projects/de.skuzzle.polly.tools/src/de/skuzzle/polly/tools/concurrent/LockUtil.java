package de.skuzzle.polly.tools.concurrent;

import java.util.concurrent.locks.Lock;


public final class LockUtil {
    
    public interface UnlockCloseable extends AutoCloseable {
        @Override
        public void close();
    }

    

    public UnlockCloseable lock(final Lock lock) {
        lock.lock();
        return new UnlockCloseable() {
            @Override
            public void close() {
                lock.unlock();
            }
        };
    }
    
    
    
    private LockUtil() {}
}
