package polly.configuration;

import java.io.IOException;


import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.eventlistener.ConfigurationListener;


/**
 * This class wraps the main {@link PollyConfiguration} class to make it suitable for
 * plugins. That is, methods {@link #store()} and {@link #setProperty(String, Object)}
 * will always throw an {@link UnsupportedOperationException}.
 * 
 * @author Simon
 * @since Beta 0.5
 * @version 27.07.2011
 */
public class ConfigurationWrapper implements Configuration {

    private Configuration config;
    
    public ConfigurationWrapper(Configuration other) {
        if (other == null) {
            throw new NullPointerException();
        }
        this.config = other;
    }
    
    
    
    @Override
    public <T> void setProperty(String name, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String readString(String name) {
        return this.config.readString(name);
    }

    @Override
    public String readString(String name, String defaultValue) {
        return this.config.readString(name, defaultValue);
    }

    @Override
    public int readInt(String name) {
        return this.config.readInt(name);
    }

    @Override
    public int readInt(String name, int defaultValue) {
        return this.config.readInt(name, defaultValue);
    }

    @Override
    public void store() throws IOException {
        throw new UnsupportedOperationException();
    }



    @Override
    public void addConfigurationListener(ConfigurationListener listener) {
        this.config.addConfigurationListener(listener);
    }



    @Override
    public void removeConfigurationListener(ConfigurationListener listener) {
        this.config.removeConfigurationListener(listener);
    }



    @Override
    public void fireConfigurationChanged() {
        this.config.fireConfigurationChanged();
    }
    
    
    
    @Override
    public void reload() {
        this.config.reload();
    }
}
