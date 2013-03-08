package de.skuzzle.polly.sdk;


import de.skuzzle.polly.sdk.exceptions.ConversationException;

/**
 * <p>Manages the creation of {@link Conversation}. As conversations must be unique in 
 * their channel-user combination, the only way to create one is using any of this class' 
 * 'create' methods.</p>
 * 
 * <p>Please note that the idle-timeouts are not checked very exact. So the actual
 * closing time can differ by few seconds.</p>
 * 
 * <p>Disposing this class will cause all active conversations to be closed as well!</p>
 * 
 * @author Simon
 * @since 0.6.0
 */
public interface ConversationManager extends Disposable {

    /**
     * Creates a new {@link Conversation} with the given user on the given channel.
     * See the documentation of the Conversation class to see how to use them.
     * 
     * The returned conversation will automatically be closed when being idle for more
     * than one minute.
     * 
     * @param ircManager The IrcManager instance to work with.
     * @param user The user this conversation is for.
     * @param channel The channel this conversation is for.
     * @return The new {@link Conversation} instance.
     * @throws ConversationException If there is already a conversation active with the
     *          same user on the same channel.
     */
    public abstract Conversation create(IrcManager ircManager, User user, String channel) 
            throws ConversationException;
    
    /**
     * Creates a new {@link Conversation} with the given user on the given channel.
     * The returned Conversation is automatically closed by polly after it has been idle
     * for the given time.
     * See the documentation of the Conversation class to see how to use them.
     * 
     * @param ircManager The IrcManager instance to work with.
     * @param user The user this conversation is for.
     * @param channel The channel this conversation is for.
     * @param idleTime The time of idling in seconds after which the returned 
     *      conversation is automatically closed.
     * @return The new {@link Conversation} instance.
     * @throws ConversationException If there is already a conversation active with the
     *          same user on the same channel.
     * @since 0.6.1
     */
    public abstract Conversation create(IrcManager ircManager, User user, String channel, 
        int idleTime) throws ConversationException;
}
