package de.skuzzle.polly.config;

/**
 * This class can be passed to {@link ConfigurationFile#open(java.io.File, 
 * ConfigValidator)} to validate the configuration after parsing it. So you can check if 
 * crucial keys exist or if required ranges are obeyed.
 * 
 * @author Simon
 */
public abstract class ConfigValidator {

    /**
     * This method validates the given configuration and should do nothing if the 
     * validation was successful. If not, the method is intended to throw a 
     * {@link ConfigException} to indicate the validation error.
     * 
     * @param config The configuration to validate.
     * @throws ConfigException If the validation was not successful.
     */
    public abstract void validate(ConfigurationFile config);
    
    
    
    /**
     * Checks if the given configurations contains all of the given sections.
     * 
     * @param config The configuration to validate.
     * @param sections Array of section names that must exist in the given configuration.
     * @throws ConfigException If the configuration does not contain all of the given
     *          sections.
     */
    public static void checkSectionsExists(ConfigurationFile config, String...sections) {
        for (String section : sections) {
            config.getSection(section);
        }
    }
    
    
    
    /**
     * Checks for existence of the given section and the key and also for the right type
     * of the value associated with that key. Additionally validates if the value is
     * greater or equal to the given minimum.
     * 
     * @param config The configuration to validate.
     * @param section The section.
     * @param key The key.
     * @param minimum The minimal value that the value associated with the given key can
     *          have.
     * @throws ConfigException If the section or the key does not exist or the value 
     *          associated with the key is no integer or the value is lower than the
     *          given minimum.
     */
    public static void checkMinimum(ConfigurationFile config, String section, String key, 
            int minimum) {
        
        if (config.getInteger(section, key) < minimum) {
            throw new ConfigException("Value for key '" + key + "' in section '" + 
                section + "' below minumum. Must be bigger than " + (minimum - 1));
        }
    }
    
    
    
    /**
     * Checks for existence of the given section and the key and also for the right type
     * of the value associated with that key. Additionally validates if the value is
     * lower or equal to the given minimum.
     * 
     * @param config The configuration to validate.
     * @param section The section.
     * @param key The key.
     * @param maximum The max value that the value associated with the given key can
     *          have.
     * @throws ConfigException If the section or the key does not exist or the value 
     *          associated with the key is no integer or the value is bigger than the
     *          given maximum.
     */
    public static void checkMaximum(ConfigurationFile config, String section, String key, 
        int maximum) {
    
        if (config.getInteger(section, key) > maximum) {
            throw new ConfigException("Value for key '" + key + "' in section '" + 
                section + "' above maximum. Must be lower than " + (maximum + 1));
        }
    }
    
    
    
    /**
     * Checks for existence of the given section and the key and also for the right type
     * of the value associated with that key. Additionally validates if the value is
     * in the given range (i.e. is lower than max and greater than min).
     * 
     * @param config The configuration to validate.
     * @param section The section.
     * @param key The key.
     * @param min The minimal value that the value associated with the given key can
     *          have.
     * @param max The max value that the value associated with the given key can
     *          have.
     * @throws ConfigException If the section or the key does not exist or the value 
     *          associated with the key is no integer or the value is not in the given 
     *          range.
     */
    public static void checkRange(ConfigurationFile config, String section, String key, 
            int min, int max) {
    
        checkMinimum(config, section, key, min);
        checkMaximum(config, section, key, max);
    }
}