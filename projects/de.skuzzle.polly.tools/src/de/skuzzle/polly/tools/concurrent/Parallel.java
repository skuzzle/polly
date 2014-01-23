package de.skuzzle.polly.tools.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.skuzzle.polly.tools.Check;


public final class Parallel {

    private final static Object LOCK = new Object();
    
    private volatile static ExecutorService executor;
    static {
        executor = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder("PARALLEL_%n%"));
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                executor.shutdown();
            }
        }));
    }
    
    
    
    public static void setExecutor(ExecutorService executor) {
        Check.objects(executor).notNull();
        synchronized (LOCK) {
            Parallel.executor.shutdown();
            Parallel.executor = executor;
        }
    }
    
    
    
    public static void run(Runnable r) {
        synchronized (LOCK) {
            Parallel.executor.execute(r);
        }
    }
}
