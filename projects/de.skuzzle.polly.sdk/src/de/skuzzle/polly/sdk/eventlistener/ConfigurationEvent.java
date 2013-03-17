package de.skuzzle.polly.sdk.eventlistener;


import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.tools.events.Event;

/**
 * This event is raised when the configuration changes.
 * 
 * @author Simon
 */
public class ConfigurationEvent extends Event<Configuration> {
    
    /**
     * Creates a new ConfigurationEvent with the given source.
     * 
     * @param source The source of this event.
     */
    public ConfigurationEvent(Configuration source) {
        super(source);
    }
}
