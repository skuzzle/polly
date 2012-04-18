package de.skuzzle.polly.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class ConfigurationFile {
    
    public static ConfigurationFile open(File file, ConfigValidator validator) 
            throws IOException, ParseException {
        
        ConfigurationFile cfg = open(file);
        validator.validate(cfg);
        return cfg;
    }
    
    
    public static ConfigurationFile open(File file) throws IOException, ParseException {
        Parser parser = new Parser(file);
        try {
            return parser.parse();
        } finally {
            parser.dispose();
        }
    }
    
    
    
    private Map<String, Section> sections;
    
    
    
    ConfigurationFile() {
        this.sections = new HashMap<String, Section>();
    }
    
    
    
    /**
     * Includes all sections and values from the given configuration into this 
     * configuration. Existent keys in equally named sections in this configuration will
     * be overridden.
     * 
     * @param file The file to include into this configuration.
     * @see #addSection(Section)
     */
    public void include(ConfigurationFile file) {
        for (Section s : file.sections.values()) {
            this.addSection(s);
        }
    }
    
    
    
    /**
     * Adds the given section to this configuration. If a section with equal name already
     * exists, the given section will be integrated into the existing, overriding all
     * keys that both configurations have in common.
     * 
     * @param section The section to add.
     */
    public void addSection(Section section) {
        Section s = this.sections.get(section.getName());
        if (s == null) {
            this.sections.put(section.getName(), section);
        } else {
            s.include(section);
        }
    }
    
    
    
    /**
     * Gets a section by its name.
     * 
     * @param name The name of the section to retrieve.
     * @return The section
     * @throws {@link ConfigException} If no section with the given name exists.
     */
    public Section getSection(String name) {
        Section s = this.sections.get(name);
        if (s == null) {
            throw new ConfigException("The section '" + name + "' does not exist");
        }
        return s;
    }
    
    
    
    /**
     * Searches for a given key in all sections and returns the first section that 
     * contains the specified key. The order in which the sections are iterated is 
     * undefined.
     * 
     * @param key The key to search for.
     * @return The first section in which a key with the given name exists.
     * @throws ConfigException If no section contains the given key.
     */
    private Section findKeySection(String key) {
        for (Section section : this.sections.values()) {
            if (section.containsKey(key)) {
                return section;
            }
        }
        throw new ConfigException("Key '" + key + "' not found");
    }
    
    
    
    /**
     * Searches for the key and tries to retrieve the associated value as a boolean.
     * 
     * @param key The key to search for.
     * @return The boolean value associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with a boolean value.
     */
    public boolean findBoolean(String key) {
        return this.findKeySection(key).getBoolean(key);
    }
    
    
    
    /**
     * Searches for the key and tries to retrieve the associated value as an integer.
     * 
     * @param key The key to search for.
     * @return The int value associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with an int value.
     */
    public int findInteger(String key) {
        return this.findKeySection(key).getInteger(key);
    }
    
    
    
    /**
     * Searches for the key and tries to retrieve the associated value as a string.
     * 
     * @param key The key to search for.
     * @return The string value associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with a string value.
     */
    public String findString(String key) {
        return this.findKeySection(key).getString(key);
    }
    
    
    
    /**
     * Searches for the key and tries to retrieve the associated value as a string list.
     * 
     * @param key The key to search for.
     * @return The string list associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with a string list.
     */
    public List<String> findStringList(String key) {
        return this.findKeySection(key).getStringList(key);
    }
    
    
    
    /**
     * Searches for the key and tries to retrieve the associated value as an int list.
     * 
     * @param key The key to search for.
     * @return The int list associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with an int list.
     */
    public List<Integer> findIntList(String key) {
        return this.findKeySection(key).getIntList(key);
    }
    
    
    
    /**
     * Searches for the key and tries to retrieve the associated value as a boolean list.
     * 
     * @param key The key to search for.
     * @return The boolean list associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with a boolean list.
     */
    public List<Boolean> findBooleanList(String key) {
        return this.findKeySection(key).getBooleanList(key);
    }
    
    
    
    /**
     * Tries to retrieve the value associated with the given key from the given section.
     * 
     * @param section The section in which the key is stored.
     * @param key The key of the value to retrieve.
     * @return The value associated with the given key in the given section as a boolean.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with a boolean value.
     */
    public boolean getBoolean(String section, String key) {
        return this.getSection(section).getBoolean(key);
    }
    
    
    
    /**
     * Tries to retrieve the value associated with the given key from the given section.
     * 
     * @param section The section in which the key is stored.
     * @param key The key of the value to retrieve.
     * @return The value associated with the given key in the given section as an int.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with an int value.
     */
    public int getInteger(String section, String key) {
        return this.getSection(section).getInteger(key);    
    }
    
    
    
    /**
     * Tries to retrieve the value associated with the given key from the given section.
     * 
     * @param section The section in which the key is stored.
     * @param key The key of the value to retrieve.
     * @return The value associated with the given key in the given section as a string.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with a string value.
     */
    public String getString(String section, String key) {
        return this.getSection(section).getString(key);
    }
    
    
    
    /**
     * Tries to retrieve the value associated with the given key from the given section.
     * 
     * @param section The section in which the key is stored.
     * @param key The key of the value to retrieve.
     * @return The value associated with the given key in the given section as a string 
     *          list.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with a string list.
     */
    public List<String> getStringList(String section, String key) {
        return this.getSection(section).getStringList(key);
    }
    
    
    
    /**
     * Tries to retrieve the value associated with the given key from the given section.
     * 
     * @param section The section in which the key is stored.
     * @param key The key of the value to retrieve.
     * @return The value associated with the given key in the given section as an int 
     *          list.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with an int list.
     */
    public List<Integer> getIntList(String section, String key) {
        return this.getSection(section).getIntList(key);
    }
    
    
    
    /**
     * Tries to retrieve the value associated with the given key from the given section.
     * 
     * @param section The section in which the key is stored.
     * @param key The key of the value to retrieve.
     * @return The value associated with the given key in the given section as a boolean 
     *          list.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with a boolean list.
     */
    public List<Boolean> getBooleanList(String section, String key) {
        return this.getSection(section).getBooleanList(key);
    }
}