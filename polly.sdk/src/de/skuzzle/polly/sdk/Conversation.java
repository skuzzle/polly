package de.skuzzle.polly.sdk;


import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.ConversationException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.model.User;


/**
 * <p>Conversations are a nice feature if any of your commands need to read multiple 
 * inputs from one user. 
 * For example, a 'RegisterCommand' could first ask for the desired username and then
 * for the desired password. Here is an example:</p>
 * 
 * <pre>
 *     Conversation c = null;
 *     try {
 *         c = Conversation.get(this.getMyPolly(), executor, channel);
 *         c.writeLine("Please insert your desired username:");
 *         String name = c.readStringLine();
 *         c.writeLine("Now insert your password");
 *         String password = c.readStringLine();
 *         c.writeLine("Please retype your password");
 *         String retype = c.readStringLine();
 *         if (!retype.equals(password)) {
 *             c.writeLine("Passwords do not match.");
 *         }
 *     } catch (ConversationException e) {
 *         // conversation could not be created because its already active
 *     } catch (IOException e) {
 *         // Error while reading input. you better close this conversation.
 *     } finally {
 *         if (c != null) {
 *             c.close();
 *         }
 *     }
 * </pre>
 * 
 * <p>As shown above you can use the {@link #get(MyPolly, User, String)} method to 
 * create a new conversation for a certain user and channel.<br/>
 * Note that there may only exist one conversation per user per channel at once. 
 * Therefore {@link #get(MyPolly, User, String)} throws a {@link ConversationException}
 * if the user tries to open a conversation that already exists.</p>
 * 
 * <p>The {@link #readLine()} and {@link #readStringLine()} methods are blocking the 
 * current thread until the user made an input on the channel this conversation is for.
 * As all commands are executed via the polly event system, this could cause some trouble
 * because many active conversations could prevent polly from executing further commands
 * if all event threads are blocked.<br/>
 * To prevent any trouble, ensure to always close a conversation when its not needed any
 * more. Furthermore, polly may shutdown idling conversations if they are blocking
 * an important event.
 * </p>
 * 
 * <p>Note that once a conversation has been closed its not possible to send or receive
 * messages using {@link #writeLine(String)} or {@link #readLine()}.</p>
 * 
 * @author Simon
 * @since 0.6.0
 */
public class Conversation extends AbstractDisposable 
            implements MessageListener, Closeable {

    /** Stores currently active declarations */
    private static List<Conversation> cache = Collections.synchronizedList(
            new ArrayList<Conversation>());
    
    
    
    /**
     * Tries to create a new conversation with a given user on a given channel. Note that
     * conversations are constrained to be unique in their channel-user combination. 
     *  
     * 
     * @param myPolly The {@link MyPolly} instance to work with.
     * @param user The user to chat with.
     * @param channel The channel to chat on. Messages from the user on other channels are
     *          ignored for this conversation.
     * @return The new Conversation instance.
     * @throws ConversationException If there is already a conversation with the same user
     *          on the same channel.
     */
    public static Conversation get(MyPolly myPolly, User user, String channel) 
                throws ConversationException {
        synchronized (mutex) {
            Conversation key = new Conversation(user, channel);
            if (cache.contains(key)) {
                throw new ConversationException("Conversation already active: " + key);
            }
            
            Conversation c = new Conversation(myPolly, user, channel);
            myPolly.irc().addMessageListener(c);
            return c;
        }
    }
    
    
    private static Object mutex = new Object();

    
    
    private MyPolly myPolly;
    private String channel;
    private User user;
    private boolean closed;
    private BlockingQueue<MessageEvent> readQueue;
    private List<MessageEvent> history;
    private ExecutorService waitExecutor;
    
    
    /**
     * Hidden constructor for creating the key conversation to look for existing 
     * conversations.
     * 
     * ATTENTION: Better not use this. Conversations created with this constructor are
     * not working!
     * 
     * @param user The user to chat with.
     * @param channel The channel to chat on.
     */
    private Conversation(User user, String channel) {
        this.channel = channel;
        this.user = user;
    }
    
    
    
    /**
     * This is the only constructor to create a valid conversation, but only when called
     * from {@link #get(MyPolly, User, String)}, as it checks for existing conversations.
     * 
     * @param myPolly The {@link MyPolly} instance to work with.
     * @param user The user to chat with.
     * @param channel The channel to chat on.
     */
    private Conversation(MyPolly myPolly, User user, String channel) {
        this.myPolly = myPolly;
        this.channel = channel;
        this.user = user;
        this.readQueue = new LinkedBlockingQueue<MessageEvent>();
        this.history = new ArrayList<MessageEvent>();
        this.waitExecutor = Executors.newSingleThreadExecutor();
    }
    
    
    
    @Override
    public void close() {
        synchronized (mutex) {
            this.myPolly.irc().removeMessageListener(this);
            cache.remove(this);
            this.closed = true;
            this.history.clear();
            this.readQueue.clear();
            this.waitExecutor.shutdown();
        }
    }
    
    
    
    public void writeLine(String message) {
        this.checkClosed();
        this.myPolly.irc().sendMessage(this.channel, message);
    }
    
    
    
    public MessageEvent readLine() throws IOException {
        this.checkClosed();
        try {
            return this.readQueue.take();
        } catch (InterruptedException e) {
            throw new IOException("Error while waiting for incoming line", e);
        }
    }
    
    
    
    public String readStringLine() throws IOException {
        return this.readLine().getMessage();
    }
    
    
    
    public List<MessageEvent> getHistory() {
        return this.history;
    }
    
    
    
    private void checkClosed() {
        if (this.closed) {
            throw new IllegalStateException("Conversation closed");
        }
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Conversation other = (Conversation) obj;
        if (channel == null) {
            if (other.channel != null)
                return false;
        } else if (!channel.equals(other.channel))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }
    
    
    
    private synchronized void onMessage(final MessageEvent e) {
        assert !this.closed : "Listener should have bene removed before closing";
        if (!e.getChannel().equals(this.channel) || 
            !e.getUser().getNickName().equals(this.user.getCurrentNickName())) {
            
            return;
        }
        this.history.add(e);
        this.waitExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Conversation.this.readQueue.put(e);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
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
        this.close();
    }
}
