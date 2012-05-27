package de.skuzzle.polly.config;


public class SectionKeyPair {

    private String section;
    private String key;
    
    
    
    public SectionKeyPair(String section, String key) {
        super();
        this.section = section;
        this.key = key;
    }


    
    public String getSection() {
        return this.section;
    }


    
    public String getKey() {
        return this.key;
    }
}