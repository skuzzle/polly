package polly.core;

import java.io.IOException;

import polly.PollyConfiguration;

import de.skuzzle.polly.sdk.Configuration;


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
}
