package de.skuzzle.polly.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



class Section extends ConfigEntry {
    
    private Map<String, ConfigEntry> entries;
    
    
    
    public Section(String name) {
        this(null, null, name);
    }
    
    
    
    public Section(Comment myBlockComment, Comment myInlineComment, String name) {
        super(myBlockComment, myInlineComment, name, null);
        this.entries = new HashMap<String, ConfigEntry>();
    }
    
    
    
    public void add(ConfigEntry entry) {
        this.entries.put(entry.getName(), entry);
    }
    
    
    
    public boolean containsKey(String key) {
        return this.entries.containsKey(key);
    }



    ConfigEntry getEntry(String key) {
        ConfigEntry entry = this.entries.get(key);
        if (entry == null) {
            throw new ConfigException("The key '" + key + "' in section '" + 
                this.getName() + "' does not exist.");
        }
        return entry;
    }
    
    
    
    public boolean getBoolean(String key) {
        Object val = this.getEntry(key).getValue();
        if (!(val instanceof Boolean)) {
            throw new ConfigException("The key '" + key + "' in section '" + 
                this.getName() + "' does not refer to a boolean value");
        }
        return (Boolean) val;
    }

    
    
    public int getInteger(String key) {
        Object val = this.getEntry(key).getValue();
        if (!(val instanceof Integer)) {
            throw new ConfigException("The key '" + key + "' in section '" + 
                this.getName() + "' does not refer to an integer value");
        }
        return (Integer) val;        
    }
    
    
    
    public String getString(String key) {
        Object val = this.getEntry(key).getValue();
        return val.toString();
    }
    
    
    
    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key) {
        Object val = this.checkList(key, String.class);
        return (ArrayList<String>) val;
    }
    
    
    
    @SuppressWarnings("unchecked")
    public List<Integer> getIntList(String key) {
        Object val = this.checkList(key, Integer.class);
        return (ArrayList<Integer>) val;
    }
    
    
    
    @SuppressWarnings("unchecked")
    public List<Boolean> getBooleanList(String key) {
        Object val = this.checkList(key, Boolean.class);
        return (ArrayList<Boolean>) val;
    }
    
    
    
    private Object checkList(String key, Class<?> listType) {
        Object val = this.getEntry(key).getValue();
        if (!(val instanceof ArrayList<?>)) {
            throw new ConfigException("The key '" + key + "' in section '" + 
                this.getName() + "' does not refer to a list " +
            		"entry");
        }
        Object o = ((ArrayList<?>) val).get(0);
        if (!listType.isAssignableFrom(o.getClass())) {
            throw new ConfigException("The key '" + key + "' in section '" + 
                this.getName() + "' does not refer to a list of " + 
                listType.getSimpleName());
        }
        return val;
    }



    public void include(Section section) {
        this.entries.putAll(section.entries);
    }
}