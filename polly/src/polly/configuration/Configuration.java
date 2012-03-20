package polly.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import polly.SortedProperties;

import de.skuzzle.polly.sdk.AbstractDisposable;


/**
 * Adapted from bibi
 * 
 * @author F.Nolte
 * @version 27.07.2011 ae73250
 */
public abstract class Configuration extends AbstractDisposable {


    protected SortedProperties props;
    private String filename;
    

    /**
     * Reads the properties from given file.
     * 
     * @param filename Filename of the properties file.
     * @throws IOException If reading the file fails.
     */
    public Configuration(String filename) throws IOException {
    	this.filename = filename;
    	// HACK: This whole config thing is totally a hack and should be fixed
    	IOException e = this.reloadFile();
    	if (e != null) {
    	    throw e;
    	}
    }
    
    
    
    protected IOException reloadFile() {
        File file = new File(this.filename);
        InputStream in = null;
        SortedProperties tmp = new SortedProperties();
        try {
            in = new FileInputStream(file);
            tmp.load(in);
            if (this.props != null) {
                this.props.clear();
            }
            this.props = tmp;
        } catch (FileNotFoundException e) {
            return e;
        } catch (IOException e) {
            return e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    return e;
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * Getter for underlying {@link Properties} instance.
     * 
     * @return The properties instance.
     */
    public SortedProperties getProps() {
        return props;
    }
    
    
    
    /**
     * Creates a new memory configuration with no default values.
     */
    public Configuration() {
        this.props = new SortedProperties();
    }
    
    
    
    /**
     * Creates a new memory configuration with default values. Note that memory
     * configurations can not be stored.
     * 
     * @param defaults The configuration default values.
     */
    public Configuration(Properties defaults) {
        this.props = new SortedProperties(defaults);
    }
    
    
    /**
     * Returns whether this is a memory config.
     * 
     * @return <code>true</code> if this config has not been read from a file
     */
    public boolean isMemory() {
        return this.filename == null;
    }
    
    
    /**
     * Writes this configuration file back to the disc.
     * 
     * @throws IOException If this is a memory configuration or if storing the file 
     *      fails for any reason.
     */
    public synchronized void store() throws IOException {
        if (this.filename == null) {
            throw new IOException("This is a memory config");
        }
    	File file = new File(this.filename);
    	OutputStream out = null;
    	try {
    		out = new FileOutputStream(file);
    		this.props.store(out, "");
    	} finally {
    		if (out != null) {
    			out.close();
    		}
    	}
    }


    
    /**
     * Must be called after reading the properties file. This method then fills the
     * custom fields with the data read from the config file.
     * 
     * @throws ConfigurationFileException If a property has an invalid value.
     */
    protected abstract void init() throws ConfigurationFileException;


    
    /**
     * Parses an integer from a string observing further semantical specifications.
     * 
     * @param value The String to parse.
     * @param lowerBound The accepted minimal value of the integer.
     * @param exceptionMessage If given values is below the lowerBound value, this
     *      will be the message of the thrown exception.
     * @return
     * @throws ConfigurationFileException If given String could not be parsed as an 
     *      integer or the integer value is below lowerBound.
     */
    protected Integer parseInteger(String value, Integer lowerBound, 
            String exceptionMessage) throws ConfigurationFileException {
        
        Integer result;
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationFileException(exceptionMessage);
        }
        if (result.compareTo(lowerBound) <= 0) {
            throw new ConfigurationFileException(
                    String.format("%s. Should be bigger than %d.",
                            exceptionMessage, lowerBound));
        }
        return result;
    }
    
    
    
    protected boolean readBoolean(String value) throws ConfigurationFileException {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") 
                || value.equalsIgnoreCase("yes")) {
            return true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off")
                ||value.equalsIgnoreCase("no")) {
            return false;
        } else {
            throw new ConfigurationFileException("invalid boolean literal: " + value);
        }
    }
}
