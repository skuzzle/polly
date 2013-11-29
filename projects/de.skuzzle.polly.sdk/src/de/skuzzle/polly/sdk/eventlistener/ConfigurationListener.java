package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


/**
 * This listener listens for configuration changes to happen and can be registered
 * at {@link Configuration}.
 * 
 * @author Simon
 * @since 0.8
 */
public interface ConfigurationListener extends EventListener {
    
    /** Notifies a listener about a ConfigurationChanged Event */
    public final static Dispatch<ConfigurationListener, ConfigurationEvent> CONFIGURATION_CHANGED = 
            new Dispatch<ConfigurationListener, ConfigurationEvent>() {
        @Override
        public void dispatch(ConfigurationListener listener, ConfigurationEvent event) {
            listener.configurationChange(event);
        }
    };
    
    

    /**
     * This method is called whenever reconfiguration is required. That is not everytime
     * a config value changes but for example if the config file itself was editet.
     * 
     * @param e Contains detailed information about this event.
     */
    public abstract void configurationChange(ConfigurationEvent e);
}
