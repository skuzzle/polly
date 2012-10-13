package de.skuzzle.polly.tools.concurrent;

import java.util.Date;


public abstract class RunLater {

    private final Thread thread;
    private final long sleepTime;
    
    
    public RunLater(String name, Date dueDate) {
        this.sleepTime = Math.max(
            0, dueDate.getTime() - System.currentTimeMillis());
        this.thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    RunLater.this.started();
                    Thread.sleep(sleepTime);
                    RunLater.this.run();
                } catch (InterruptedException e) {
                    RunLater.this.interrupted();
                } finally {
                    RunLater.this.finished();
                }
            }
        }, name);
    }
    
    
    
    public RunLater(String name, long timespan) {
        this(name, new Date(System.currentTimeMillis() + timespan));
    }
    
    
    
    public final RunLater start() {
        this.thread.start();
        return this;
    }
    
    
    
    public void stop() {
        this.thread.interrupt();
    }
    
    
    public void started() {}
    
    
    
    public abstract void run();
    
    
    
    public void interrupted() {}
    
    
    
    public void finished() {}
}
