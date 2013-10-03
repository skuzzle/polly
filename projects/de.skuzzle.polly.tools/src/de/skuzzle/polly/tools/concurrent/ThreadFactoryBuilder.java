package de.skuzzle.polly.tools.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;


public class ThreadFactoryBuilder implements ThreadFactory {

    private final static Pattern PATTERN = Pattern.compile("%n%");
    private String name;
    private int threadNum;
    private int priority;
    private boolean daemon;
    private UncaughtExceptionHandler exceptionHandler;
    private boolean daemon;
    
    
    
    public ThreadFactoryBuilder() {
        this("");
    }
    
    
    public ThreadFactoryBuilder(String name) {
        this.name = name;
        this.priority = Thread.NORM_PRIORITY;
    }
    
    
    public ThreadFactoryBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    
    
    public ThreadFactoryBuilder setPriority(int priority) {
        this.priority = priority;
        return this;
    }
    
    
    
    public ThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler h) {
        this.exceptionHandler = h;
        return this;
    }
    
    
    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }
    
    
    @Override
    public Thread newThread(Runnable r) {
        String name = ThreadFactoryBuilder.PATTERN.matcher(this.name).replaceFirst(
                "" + this.threadNum++);
        final Thread result = new Thread(r, name);
        result.setDaemon(this.daemon);
        result.setPriority(this.priority);
        result.setDaemon(this.daemon);
        if (this.exceptionHandler != null) {
            result.setUncaughtExceptionHandler(this.exceptionHandler);
        }
        return result;
    }


    public ThreadFactory setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

}