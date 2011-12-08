package polly.util;


public interface Stopwatch {
    
    public abstract void start();
    
    public abstract long stop();
    
    public abstract long getDifference();

}
