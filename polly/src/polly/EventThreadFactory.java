package polly;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ThreadFactory for polly event threads. This is specially used to set the thread names.
 * 
 * @author Simon
 * @version TODO
 * @since Beta 0.5
 */
public class EventThreadFactory implements ThreadFactory {

    private static AtomicInteger threadId = new AtomicInteger(1);
    
    private String prefix;
    
    
    
    public EventThreadFactory() {
        this("EVENT");
    }
    
    public EventThreadFactory(String prefix) {
        this.prefix = prefix;
    }
    
    
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, this.prefix + "_THREAD_" + threadId.getAndIncrement());
        return t;
    }

}
