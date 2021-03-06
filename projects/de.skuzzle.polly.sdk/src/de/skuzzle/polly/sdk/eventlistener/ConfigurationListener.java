package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.jeve.Listener;

/**
 * This listener listens for configuration changes to happen and can be registered
 * at {@link Configuration}.
 * 
 * @author Simon
 * @since 0.8
 */
public interface ConfigurationListener extends Listener {
    
    /**
     * This method is called whenever reconfiguration is required. That is not everytime
     * a config value changes but for example if the config file itself was editet.
     * 
     * @param e Contains detailed information about this event.
     */
    public abstract void configurationChange(ConfigurationEvent e);
}
