package de.skuzzle.polly.config;

import java.util.EventListener;


public interface ConfigurationListener extends EventListener {

    public abstract void configurationChanged(ConfigurationEvent e);
}
