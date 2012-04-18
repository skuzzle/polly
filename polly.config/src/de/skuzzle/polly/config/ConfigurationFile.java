package de.skuzzle.polly.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;




public class ConfigurationFile {
    
    /**
     * 
     * @param file The file to read.
     * @param validator
     * @return The ConfigurationFile
     * @throws IOException If an I/O error occurs. 
     * @throws ParseException If the given file is not formatted properly.
     */
    public static ConfigurationFile open(File file, ConfigValidator validator) 
            throws IOException, ParseException {
        
        ConfigurationFile cfg = open(file);
        validator.validate(cfg);
        return cfg;
    }
    
    
    
    /**
     * 
     * @param file The file to read.
     * @return The ConfigurationFile
     * @throws IOException If an I/O error occurs. 
     * @throws ParseException If the given file is not formatted properly.
     */
    public static ConfigurationFile open(File file) throws IOException, ParseException {
        Parser parser = new Parser(file);
        try {
            return parser.parse();
        } finally {
            parser.dispose();
        }
    }
    
    
    
    private File path;
    private Map<String, Section> sections;
    private List<ConfigurationFile> includes;
    
    
    
    ConfigurationFile(File path) {
        this.path = path;
        this.includes = new LinkedList<ConfigurationFile>();
        this.sections = new HashMap<String, Section>();
    }
    
    
    
    public File getPath() {
        return this.path;
    }
    
    
    
    /**
     * Includes all sections and values from the given configuration into this 
     * configuration. Existent keys in equally named sections in this configuration will
     * be overridden.
     * 
     * @param file The file to include into this configuration.
     * @throws ConfigException If a configuration with the same path is added.
     * @see #addSection(Section)
     */
    public void addInclude(ConfigurationFile file) {
        if (file.getPath().equals(this.getPath())) {
            throw new ConfigException("Configuration can not include itself");
        }
        this.includes.add(file);
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
     * Gets a section by its name. If the section is not found in this configuration,
     * it is looked up in the parent configuration.
     * 
     * @param name The name of the section to retrieve.
     * @return The section
     * @throws {@link ConfigException} If no section with the given name exists.
     */
    public Section getSection(String name) {
        Section s = this.getSectionHelper(name);
        
        if (s == null) {
            throw new ConfigException("The section '" + name + "' does not exist");
        }
        return s;
    }
    
    
    
    private Section getSectionHelper(String name) {
        Section s = this.sections.get(name);
        if (s == null) {
            for (ConfigurationFile cfg : this.includes) {
                s = cfg.getSection(name);
                if (s != null) {
                    return s;
                }
            }
        }
        return null;
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
        Section s = this.findKeySectionHelper(key);
        if (s == null) {
            throw new ConfigException("Key '" + key + "' not found");
        }
        return s;
    }
    
    
    
    private Section findKeySectionHelper(String key) {
        for (Section section : this.sections.values()) {
            if (section.containsKey(key)) {
                return section;
            }
        }
        
        for (ConfigurationFile cfg : this.includes) {
            Section s = cfg.findKeySectionHelper(key);
            if (s != null) {
                return s;
            }
        }
        return null;
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
     * Searches for the key and tries to retrieve the associated value as a double.
     * 
     * @param key The key to search for.
     * @return The boolean value associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with a double value.
     */
    public double findDouble(String key) {
        return this.findKeySection(key).getDouble(key);
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
     * Searches for the key and tries to retrieve the associated value as a double list.
     * 
     * @param key The key to search for.
     * @return The boolean list associated with that key.
     * @throws ConfigException If the given key does not exist in any section or is not 
     *          associated with a double list.
     */
    public List<Double> findDoubleList(String key) {
        return this.findKeySection(key).getDoubleList(key);
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
     * @return The value associated with the given key in the given section as a double.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with a double value.
     */
    public double getDouble(String section, String key) {
        return this.getSection(section).getDouble(key);    
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
     * @return The value associated with the given key in the given section as a double 
     *          list.
     * @throws ConfigException If the given section does not exist or the key does not 
     *          exist in that section or the key is not associated with a double list.
     */
    public List<Double> getDoubleList(String section, String key) {
        return this.getSection(section).getDoubleList(key);
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
    
    
    
    private String format() {
        StringBuilder b = new StringBuilder();
        
        for (ConfigurationFile cfg : this.includes) {
            b.append("@include ");
            b.append(cfg.getPath());
            b.append("\n");
        }
        b.append("\n");
        
        for (Section section : this.sections.values()) {
            if (section.getBlockComment() != null) {
                b.append("    ");
                b.append(section.getBlockComment().toString());
                b.append("\n");
            }
            b.append("[");
            b.append(section.getName());
            b.append("]");
            if (section.getInlineComment() != null) {
                b.append(" ");
                b.append(section.getInlineComment().toString());
            }
            b.append("\n");
            
            for (ConfigEntry entry : section.getEntries().values()) {
                if (entry.getBlockComment() != null) {
                    b.append("    ");
                    b.append(entry.getBlockComment().toString());
                    b.append("\n");
                }
                b.append("    ");
                b.append(entry.getName());
                b.append(" = ");
                b.append(entry.getValue().toString());
                if (entry.getInlineComment() != null) {
                    b.append(" ");
                    b.append(entry.getInlineComment());
                }
                b.append("\n");
            }
            b.append("\n");
        }
        
        return b.toString();
    }
    
    
    
    /**
     * Stores this configuration under the same path as it was read from.
     * 
     * @throws IOException If writing the configuration fails.
     */
    public void store() throws IOException {
        this.store(this.path);
    }
    
    
    
    /**
     * Stores this configuration as the given file, but does not change the 
     * {@link #getPath()} attribute for this configuration.
     * 
     * @param file The file where to store this configuration.
     * @throws IOException If writing the configuration fails.
     */
    public void store(File file) throws IOException {
        PrintWriter w = null;
        try {
            w = new PrintWriter(file);
            w.print(this.format());
        } finally {
            if (w != null) { w.close(); }
        }
    }
}