package polly.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.Conversation;
import de.skuzzle.polly.sdk.ConversationManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.ConversationException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.model.User;



public class ConversationManagerImpl extends AbstractDisposable implements ConversationManager {
    
    private static Logger logger = Logger.getLogger(
                    ConversationManagerImpl.class.getName());
    
    
    
    private static class ConvThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "CONVERSATION");
            return t;
        }
    }
    
    
    
    private static class ConversationImpl extends AbstractDisposable 
                implements Conversation, MessageListener {
        
        private boolean closed;
        private List<MessageEvent> history;
        private BlockingQueue<MessageEvent> readQueue;
        private Executor waitExecutor;
        private String channel;
        private User user;
        private MyPolly myPolly;
            
        
        public ConversationImpl(Executor executor, MyPolly myPolly, User user, 
                String channel) {
            
            this.waitExecutor = executor;
            this.myPolly = myPolly;
            this.user = user;
            this.channel = channel;
            this.readQueue = new LinkedBlockingQueue<MessageEvent>();
            this.history = new ArrayList<MessageEvent>();
        }
        
        
        
        private void checkClosed() {
            if (this.closed) {
                throw new IllegalStateException("ConversationImpl closed");
            }
        }
        
        
        
        @Override
        public String readStringLine() throws IOException {
            return this.readLine().getMessage();
        }

        
        
        @Override
        public MessageEvent readLine() throws IOException {
            this.checkClosed();
            try {
                return this.readQueue.take();
            } catch (InterruptedException e) {
                throw new IOException("Error while waiting for incoming line", e);
            }
        }

        
        
        @Override
        public void writeLine(String line) {
            this.checkClosed();
            this.myPolly.irc().sendMessage(this.channel, line);
        }

        
        
        @Override
        public List<MessageEvent> getHistory() {
            return this.history;
        }
        
        
        
        private synchronized void onMessage(final MessageEvent e) {
            assert !this.closed : "Listener should have been removed before closing";
            if (!e.getChannel().equals(this.channel) || 
                !e.getUser().getNickName().equals(this.user.getCurrentNickName())) {
                
                return;
            }
            this.history.add(e);
            this.waitExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ConversationImpl.this.readQueue.put(e);
                    } catch (InterruptedException e1) {
                        logger.warn("Interrupted while reading", e1);
                    }
                }
            });
        }
        
        

        @Override
        public void publicMessage(MessageEvent e) {
            this.onMessage(e);
        }

        
        
        @Override
        public void privateMessage(MessageEvent e) {
            this.onMessage(e);
        }

        
        
        @Override
        public void actionMessage(MessageEvent e) {}

        
        
        @Override
        protected void actualDispose() throws DisposingException {
            synchronized (crossMutex) {
                this.myPolly.irc().removeMessageListener(this);
                this.closed = true;
                this.history.clear();
                this.readQueue.clear();
            }
        }
        
        
        
        @Override
        public void close() {
            try {
                this.dispose();
            } catch (DisposingException e) {
                logger.error("Error while disposing", e);
            }
        }
        
        
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConversationImpl other = (ConversationImpl) obj;
            if (channel == null) {
                if (other.channel != null)
                    return false;
            } else if (!channel.equals(other.channel))
                return false;
            if (user == null) {
                if (other.user != null)
                    return false;
            } else if (!user.getCurrentNickName().equals(other.user.getCurrentNickName()))
                return false;
            return true;
        }
    }
    
    
    
    
    // Object to synchronize on when closing or creating conversations
    private static Object crossMutex = new Object();
    
    
    private ExecutorService convExecutor;
    private MyPolly myPolly;
    private List<Conversation> cache;
    
    
    
    public ConversationManagerImpl() {
        this.convExecutor = Executors.newCachedThreadPool(new ConvThreadFactory());
        this.cache = Collections.synchronizedList(new LinkedList<Conversation>());
    }
    
    

    @Override
    public Conversation create(MyPolly myPolly, User user, String channel)
            throws ConversationException {
        
        assert this.myPolly != null : "Assure to set MyPolly";
        synchronized (crossMutex) {
            Conversation key = new ConversationImpl(null, null, user, channel);
            if (this.cache.contains(key)) {
                throw new ConversationException("Conversation already active");
            }
            
            ConversationImpl c = new ConversationImpl(this.convExecutor, this.myPolly, 
                user, channel);
            this.myPolly.irc().addMessageListener(c);
            this.cache.add(c);
            logger.debug("Created new conversation with " + user.getCurrentNickName() + 
                    " on channel " + channel);
            return c;
        }

    }
    
    
    
    public void setMyPolly(MyPollyImpl myPolly) {
        this.myPolly = myPolly;
    }
    
    

    @Override
    protected void actualDispose() throws DisposingException {
        try {
            for (Conversation conv : this.cache) {
                conv.dispose();
            }
            this.convExecutor.shutdown();
        } catch (Exception e) {
            throw new DisposingException(e);
        }
    }
}
