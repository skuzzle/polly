package polly.network.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ProtocolObject implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    
    
    private int id;
    private long timestamp;
    private long receivedAt;
    private Map<String, Object> payload;

    
    public ProtocolObject() {
        this.id = ID_COUNTER.getAndIncrement();
        this.timestamp = System.currentTimeMillis();
        this.payload = new HashMap<String, Object>();
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    public long travelTime() {
        return this.receivedAt - this.timestamp;
    }
    
   
    public long getTimestamp() {
        return this.timestamp;
    }
    

    
    public long getReceivedAt() {
        return this.receivedAt;
    }

    
    
    public void setReceivedAt(long receivedAt) {
        this.receivedAt = receivedAt;
    }
    
    
    public Map<String, Object> getPayload() {
        return this.payload;
    }
    
}