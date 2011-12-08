package de.skuzzle.polly.sdk;

import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;


/**
 * This filter is used to filter irc logmessages if irc logging is enabled 
 * ({@link Configuration#IRC_LOGGING}). 
 * 
 * @author Simon
 * @since 0.6.1
 */
public interface IrcLogFilter {

    /**
     * Defines rules to discard {@link MessageEvent}
     * 
     * @param e The incoming {@link MessageEvent}.
     * @return <code>true</code> if the event should be discarded and should not show
     *          up in the polly log files.
     */
    public boolean discard(MessageEvent e);
    
    
    
    /**
     * Defines rules to discard {@link NickChangeEvent}
     * 
     * @param e The incoming {@link NickChangeEvent}.
     * @return <code>true</code> if the event should be discarded and should not show
     *          up in the polly log files.
     */
    public boolean discard(NickChangeEvent e);
    
    
    
    /**
     * Defines rules to discard {@link ChannelEvent}
     * 
     * @param e The incoming {@link ChannelEvent}.
     * @return <code>true</code> if the event should be discarded and should not show
     *          up in the polly log files.
     */
    public boolean discard(ChannelEvent e);
}