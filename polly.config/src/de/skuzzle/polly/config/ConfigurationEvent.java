package de.skuzzle.polly.config;


public class ConfigurationEvent {

    private ConfigurationFile source;
    private String section;
    private String key;
    
    
    
    public ConfigurationEvent(ConfigurationFile source, String section,
        String key) {
        super();
        this.source = source;
        this.section = section;
        this.key = key;
    }


    
    public ConfigurationFile getSource() {
        return this.source;
    }


    
    public String getSection() {
        return this.section;
    }



    public String getKey() {
        return this.key;
    }
}
