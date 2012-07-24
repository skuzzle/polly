package polly.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.eventlistener.ConfigurationListener;


public class ConfigurationImpl implements Configuration {

    private Properties properties;
    
    
    
    public ConfigurationImpl(File cfgFile) 
                throws FileNotFoundException, IOException {
        this();
        this.properties.load(new FileInputStream(cfgFile));
    }
    
    
    
    public ConfigurationImpl() {
        this.properties = new Properties();
    }
    
    
    
    @Override
    public <T> void setProperty(String name, T value) {
        this.properties.setProperty(name, value.toString());
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
        String[] list = prop.split("[,;]");
        return Arrays.asList(list);
    }

    
    
    @Override
    public void addConfigurationListener(ConfigurationListener listener) {
    }

    
    
    @Override
    public void removeConfigurationListener(ConfigurationListener listener) {
    }

    
    
    @Override
    public void fireConfigurationChanged() {
    }
}
