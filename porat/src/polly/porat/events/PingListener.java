package polly.porat.events;

import java.util.EventListener;



public interface PingListener extends EventListener {

    public abstract void ping(int latency);
}