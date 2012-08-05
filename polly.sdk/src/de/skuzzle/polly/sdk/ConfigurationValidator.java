package de.skuzzle.polly.sdk;

import javax.naming.ConfigurationException;

/**
 * This class can be used to validate a configuration before using it. 
 * Validators are passed to the 
 * {@link ConfigurationProvider#open(String, ConfigurationValidator)} method,
 * which then calls this instance's {@link #validate(Configuration)} method.
 * 
 * @author Simon
 * @since 0.9.1
 */
public interface ConfigurationValidator {

    /**
     * Performs validity checks for the given configuration instance. If the 
     * configuration is valid, this metho should do nothing. Otherwise, a 
     * {@link ConfigurationException} must be thrown to indicate the validation
     * error.
     * 
     * @param config The configuration to validate.
     * @throws ConfigurationException If the configuration is not valid.
     */
    public void validate(Configuration config) throws ConfigurationException;
}
