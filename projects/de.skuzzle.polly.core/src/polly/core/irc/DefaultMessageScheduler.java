package polly.core.irc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.exceptions.DisposingException;

public class DefaultMessageScheduler extends Thread implements MessageScheduler {

    private IrcManagerImpl ircManager;
    private AtomicBoolean shutdownFlag;
    private Map<Object, AtomicLong> counter;
    private PriorityBlockingQueue<CompareKey> messageQueue;
    private int delay;

    private class CompareKey implements Comparable<CompareKey> {

        private long value;
        private MessageEvent message;



        public CompareKey(Object key, MessageEvent message) {
            AtomicLong al = counter.get(key);
            this.value = al == null ? 0 : al.get();
            this.message = message;
        }



        public MessageEvent getMessage() {
            return this.message;
        }



        @Override
        public int compareTo(CompareKey o) {
            return o.value <= this.value ? 1 : -1;
        }
    }



    public DefaultMessageScheduler(IrcManagerImpl ircManager, int delay) {
        this.ircManager = ircManager;
        this.delay = delay;
        this.counter = new ConcurrentHashMap<Object, AtomicLong>();
        this.messageQueue = new PriorityBlockingQueue<CompareKey>();
        this.shutdownFlag = new AtomicBoolean();
    }



    @Override
    public void addMessage(String channel, String message, Object source) {
        MessageEvent e = new MessageEvent(null, null, channel, message);
        AtomicLong c = this.counter.get(source);
        if (c == null) {
            c = new AtomicLong();
            this.counter.put(source, c);
        }
        c.incrementAndGet();
        this.messageQueue.add(new CompareKey(source, e));
    }



    @Override
    public void run() {
        System.err.println("started");
        while (!this.shutdownFlag.get()) {
            System.out.println("before poll");
            try {
                CompareKey key = this.messageQueue.take();
                System.out.println("polled");
                this.ircManager.sendMessage(key.getMessage().getChannel(), 
                    key.getMessage().getMessage());

                Thread.sleep(this.delay);
            } catch (InterruptedException e1) {
                return;
            }
        }
    }



    @Override
    public boolean isDisposed() {
        return this.shutdownFlag.get();
    }



    @Override
    public void dispose() throws DisposingException {
        this.shutdownFlag.set(true);
        this.interrupt();
    }



    @Override
    public void setMessageDelay(int delay) {
    }
}
