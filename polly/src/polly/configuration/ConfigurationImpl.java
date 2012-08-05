package polly.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationValidator;
import de.skuzzle.polly.sdk.eventlistener.ConfigurationEvent;



public class ConfigurationImpl implements Configuration {

    private final static Logger logger = Logger.getLogger(
            ConfigurationImpl.class.getName());
    
    private Properties properties;
    private ConfigurationProviderImpl parent;
    private ConfigurationValidator validator;
    
    
    
    public ConfigurationImpl(File cfgFile, ConfigurationProviderImpl parent) 
                throws FileNotFoundException, IOException {
        this(parent);
        this.properties.load(new FileInputStream(cfgFile));
    }
    
    
    
    public ConfigurationImpl(ConfigurationProviderImpl parent) {
        this.parent = parent;
        this.properties = new Properties();
    }
    
    
    
    public void setValidator(ConfigurationValidator validator) {
        this.validator = validator;
    }
    
    
    
    @Override
    public ConfigurationValidator getValidator() {
        return this.validator;
    }
    
    
    
    @Override
    public boolean isValidated() {
        return this.validator != null;
    }
    
    
    
    @Override
    public <T> void setProperty(String name, T value) {
        this.properties.setProperty(name, value.toString());
        if (parent == null) {
            logger.warn("Tried to dispatch a ConfigurationEvent, but no parent was set!");
        } else {
            this.parent.fireConfigurationChanged(new ConfigurationEvent(this));
        }
    }
    
    

    @Override
    public String readString(String name) {
        return this.properties.getProperty(name);
    }

    
    
    @Override
    public String readString(String name, String defaultValue) {
        return this.properties.getProperty(name, defaultValue);
    }

    
    
    @Override
    public int readInt(String name) {
        return Integer.parseInt(this.readString(name));
    }
    
    

    @Override
    public int readInt(String name, int defaultValue) {
        try {
            return this.readInt(name);
        } catch (Exception e) {
            return 0;
        }
    }

    

    @Override
    public boolean readBoolean(String name) {
        return this.readString(name).equals("true");
    }
    
    
    
    
    @Override
    public List<String> readStringList(String name) {
        String prop = this.readString(name);
        if (prop == null) {
            return new LinkedList<String>();
        }
        String[] list = prop.split(",");
        return Arrays.asList(list);
    }
    
    
    
    @Override
    public List<Integer> readIntList(String name) {
        String prop = this.readString(name);
        if (prop == null) {
            return new LinkedList<Integer>();
        }
        String[] parts = prop.split(",");
        ArrayList<Integer> result = new ArrayList<Integer>(parts.length);
        for (String s : parts) {
            result.add(Integer.parseInt(s));
        }
        return result;
    }
}
