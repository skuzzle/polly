package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.IrcManager;


/**
 * This class represents a channel mode change event. It holds the {@link IrcUser} who
 * changed the modes for a certain channel.
 * 
 * @author Simon
 * @since Beta 0.5
 * @version 1.0
 */
public class ChannelModeEvent extends ChannelEvent {
    
    /**
     * Constant representing the mode +/-c.
     */
    public final static char COLORS = 'c';
    
    /**
     * Constant representing the mode +/-m.
     */
    public final static char MODERATED = 'm';
    
    /**
     * Constant representing the mode +/-i.
     */
    public final static char INVITE_ONLY = 'i';
    
    /**
     * Constant representing the mode +/-s.
     */
    public final static char SECRET = 's';
    
    /**
     * Constant representing the mode +/-t.
     */
    public final static char TOPIC_LOCK = 't';
    
    /**
     * Constant representing the mode +/-n.
     */
    public final static char EXTERNAL_MESSAGES = 'n';
    
    /**
     * Constant representing the mode +/-r.
     */
    public final static char REGISTERED = 'r';
    
    /**
     * Constant representing the mode +/-p.
     */
    public final static char PRIVATE = 'p';
    
    /**
     * Constant representing the mode +/-k
     */
    public final static char PASSWORD = 'k';
    
    /**
     * Constant representing the mode +/-l.
     */
    public final static char LIMITED = 'l';

    
    private String mode;
    private String[] parameters;
    
    
    
    /**
     * Creates a new ChannelModeEvent.
     * @param source The sourc {@link IrcManager}.
     * @param user The {@link IrcUser} who changed the modes.
     * @param channel The channel which modes have been changed.
     * @param mode The String containing the changed modes.
     */
    public ChannelModeEvent(IrcManager source, IrcUser user, String channel, 
            String mode) {
        super(source, user, channel);
        String[] parts = mode.split(" ", 2);
        this.mode = parts[0];
        if (parts.length == 1) {
            this.parameters = new String[0];
        } else if (parts.length == 2) {
            this.parameters = parts[1].split(" ");
        }
    }
    
    
    
    /**
     * Gets the String containing the changed modes, without parameters.
     * @return The mode String.
     */
    public String getMode() {
        return this.mode;
    }
    
    
    
    /**
     * Determines whether a certain mode has been changed within this event.
     * @param mode The mode to check.
     * @return <code>true</code> if the given mode has been changed within this event.
     */
    public boolean modeChanged(char mode) {
        return this.mode.indexOf(mode) != -1;
    }
    
    
    
    /**
     * Determines whether a certain mode has been set within this event.
     * @param mode The mode to check.
     * @return <code>true</code> if the given mode has been set within this event.
     */
    public boolean modeSet(char mode) {
        int i = this.mode.indexOf(mode);
        if (i < 0) {
            return false;
        }
        
        while (i > 0) {
            if (this.mode.charAt(--i) == '+') {
                return true;
            } else if (this.mode.charAt(i)  == '-') {
                return false;
            }
        }
        return false;
    }
    
    
    
    /**
     * Determines whether a certain mode has been removed within this event.
     * @param mode The mode to check.
     * @return <code>true</code> if the given mode has been removed within this event.
     */
    public boolean modeRemoved(char mode) {
        int i = this.mode.indexOf(mode);
        if (i < 0) {
            return false;
        }
        
        while (i > 0) {
            if (this.mode.charAt(--i) == '-') {
                return true;
            } else if (this.mode.charAt(i) == '+') {
                return false;
            }
        }
        return false;
    }
    
    
    
    /**
     * Gets the parameters for the changed mode. This will return a zero length array
     * if no parameters exists.
     * @return An array containing the parameters for the changed modes in order of their
     *      appearance.
     */
    public String[] getParameters() {
        return this.parameters;
    }
}
