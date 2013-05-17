package de.skuzzle.polly.sdk;

import java.io.IOException;
import java.util.List;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;


/**
 * <p>Conversations provide an easy way to read several inputs from one user within one 
 * command. It can be used similar to an {@link InputStream}. You can create a
 * new conversation using any of the create methods of the {@link ConversationManager}
 * </p>
 * 
 * <p>Calling any of the {@link #readLine()} methods will cause the current thread to 
 * block until the user for which this conversation was created wrote a line on the 
 * channel for which this conversation was created. {@link #writeLine(String)} is a 
 * convenient way for replying (it wraps 
 * {@link IrcManager#sendMessage(String, String, Object)}</p>
 * 
 * <p>Message from other users or from the same user on a different channel are ignored 
 * by the conversation.</p>
 * 
 * <p>Please note: As Conversations are thread-blocking and usually called during 
 * execution of a command they may block pollys event system. If all event threads are
 * blocked, no further {@link MessageEvent}s can be fired thus causing polly to run into
 * a deadlock (as the conversations are waiting for the MessageEvent which 
 * unblocks them).</p>
 * 
 * <p>To prevent any trouble ensure to always close conversations and using them 
 * thoughtfully. Additionally, polly may shutdown idling conversations in order to 
 * gain a further event threads. You may also define a timeout after which your
 * conversation is closed automatically.</p>
 * 
 * <p>Once a conversation has been closed, it cannot further be used. You can check
 * whether the conversation can still be used you may use 
 * {@link #isDisposed()}</p>
 * 
 * <p>Here is an example of how to create and use conversations:</p>
 * <pre>
 *  protected boolean executeOnBoth(User executer, String channel,
 *      Signature signature) throws CommandException {
 *      
 *      IrcUser u = new IrcUser(executer.getCurrentNickName(), "", "");
 *      Conversation c = null;
 *      try {
 *          c = this.getMyPolly().conversations().create(getMyPolly(), executer, channel);
 *
 *          c.writeLine("Please insert your desired username:");
 *          String name = c.readStringLine();
 *          c.writeLine("Now insert your password");
 *          String password = c.readStringLine();
 *          c.writeLine("Please retype your password");
 *          String retype = c.readStringLine();
 *          if (!retype.equals(password)) {
 *              c.writeLine("Passwords do not match.");
 *          }
 *          c.writeLine(c.getHistory().toString());
 *      } catch (Exception e) {
 *          this.reply(channel, e.getMessage());
 *      } finally {
 *          if (c != null) {
 *              c.close();
 *          }
 *      }
 *      
 *      return false;
 *  }
 * </pre>
 *  
 * @author Simon
 *
 */
public interface Conversation extends Disposable {

    
    /**
     * Wrapper method for {@link #readLine()} which returns the message-string from the
     * incoming {@link MessageEvent}.
     * 
     * @return The String the user wrote.
     * @throws IOException If this thread was interrupted while waiting for the incoming
     *          message or an invalid cross thread call happened.
     * @throws InterruptedException If the conversation is closed while waiting for an
     *          incoming line.
     * @throws IllegalStateException If this Conversation is closed.
     */
    public abstract String readStringLine() throws IOException, InterruptedException;
    
    /**
     * Waits until the user for which this conversation was created wrote a line on the
     * channel for which this conversation was created.
     * One conersation can only be read from one thread. If you call readLine() from two
     * different threads, this will cause an IOException.
     * 
     * @return The {@link MessageEvent} of the incoming message.
     * @throws IOException If this thread was interrupted while waiting for the incoming
     *          message or an invalid cross thread call happened.
     * @throws InterruptedException If the conversation is closed while waiting for an
     *          incoming line.
     * @throws IllegalStateException If this Conversation is closed.
     */
    public abstract MessageEvent readLine() throws IOException, InterruptedException;
    
    
    /**
     * Writes the given line on the channel for which this conversation was created. This
     * method is just a wrapper for {@link IrcManager#sendMessage(String, String)}.
     * 
     * @param line The line to send to the channel.
     * @throws IllegalStateException If this Conversation is closed.
     */
    public abstract void writeLine(String line);
    
    
    /**
     * Gets a list of all {@link MessageEvent}s that this conversation received.
     * 
     * @return A list of {@link MessageEvent}s
     */
    public abstract List<MessageEvent> getHistory();
    
    
    /**
     * Determines whether this conversation is idle. An idle conversation will be
     * automatically closed by polly.
     * 
     * @return If this conversation is idle.
     */
    public abstract boolean isIdle();
    
    
    /**
     * <p>Closes this conversation. After closing, you cannot use this conversation any
     * more to read or write lines.</p>
     * <p>This is a no-exception wrapper for {@link #dispose()}</p>
     */
    public abstract void close();
}
