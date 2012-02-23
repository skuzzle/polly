package polly.network.protocol;

import java.io.Serializable;


public class LogItem implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private long timestamp;
    private String thread;
    private String source;
    private Object message;
    private String level;
    
    
    public LogItem(long timestamp, String level, String thread, String source, 
            Object message) {
        super();
        this.timestamp = timestamp;
        this.level = level;
        this.thread = thread;
        this.source = source;
        this.message = message;
    }


    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    
    
    public String getLevel() {
        return this.level;
    }


    
    public String getThread() {
        return this.thread;
    }


    
    public String getSource() {
        return this.source;
    }


    
    public Object getMessage() {
        return this.message;
    }
}