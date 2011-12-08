package polly.util.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;


public class ThreadFactoryBuilder implements ThreadFactory {

    private final static Pattern PATTERN = Pattern.compile("%n%");
    private String name;
    private int threadNum;
    private UncaughtExceptionHandler exceptionHandler;
    
    
    
    public ThreadFactoryBuilder() {
        this.name = "";
    }
    
    
    public ThreadFactoryBuilder(String name) {
        this.name = name;
    }
    
    
    public ThreadFactoryBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    
    
    public ThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler h) {
        this.exceptionHandler = h;
        return this;
    }
    
    
    
    @Override
    public Thread newThread(Runnable r) {
        ThreadFactoryBuilder.PATTERN.matcher(this.name).replaceFirst(
                "" + this.threadNum++);
        Thread result = new Thread(r, this.name);
        if (this.exceptionHandler != null) {
            result.setUncaughtExceptionHandler(this.exceptionHandler);
        }
        return result;
    }

}