package polly.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;

/*
 * ISSUE: 0000049
 */

public class MessageThread extends Thread {

    private static Logger logger = Logger.getLogger(MessageThread.class.getName());

    private IrcManagerImpl ircManager;
    private Semaphore sema;
    private ConcurrentMap<Object, LinkedList<MessageEvent>> messageQueue;
    private int messageDelay;
    
    
    public MessageThread(IrcManagerImpl ircManager, int messageDelay) {
        super("IRC_MESSAGE_SCHEDULER");
        this.ircManager = ircManager;
        this.messageQueue = new ConcurrentHashMap<Object, LinkedList<MessageEvent>>();
        this.sema = new Semaphore(0);
        this.messageDelay = messageDelay;
    }
    
    
    
    public void addMessage(String channel, String message, Object source) {
        synchronized (this.messageQueue) {
            LinkedList<MessageEvent> msgs = this.messageQueue.get(source);
            if (msgs == null) {
                msgs = new LinkedList<MessageEvent>();
                this.messageQueue.put(source, msgs);
            }
            msgs.add(new MessageEvent(this.ircManager, null, channel, message));
            this.sema.release();
        }
    }
    
    
    
    @Override
    public void run() {
        while (true) {
            try {
                logger.trace("Waiting for message input");
                this.sema.acquire();
                logger.trace("Message available");
            } catch (InterruptedException e) {
                logger.error("", e);
            }
            
            
            synchronized (this.messageQueue) {
                logger.info("QUEUED: " + this.messageQueue.size());
                Iterator<Entry<Object, LinkedList<MessageEvent>>> it = 
                        this.messageQueue.entrySet().iterator();
            
                while (it.hasNext()) {
                    Entry<Object, LinkedList<MessageEvent>> entry = it.next();
                    LinkedList<MessageEvent> msgs = entry.getValue();
                    
                    MessageEvent next = msgs.pollFirst();
                    this.ircManager.sendMessage(next.getChannel(), next.getMessage());
                    try {
                        Thread.sleep(this.messageDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.trace("Sent message via scheduler thread");
                    
                    if (msgs.isEmpty()) {
                        it.remove();
                    }
                }
            }
        }
    }
}