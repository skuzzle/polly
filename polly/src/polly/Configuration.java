package polly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


/**
 * Adapted from bibi
 * 
 * @author F.Nolte
 *
 */
public abstract class Configuration {


    protected Properties props;
    private String filename;
    

    /**
     * Reads the properties from given file.
     * 
     * @param filename Filename of the properties file.
     * @throws IOException If reading the file fails.
     */
    public Configuration(String filename) throws IOException {
    	this.filename = filename;
        File file = new File(filename);
        InputStream in = null;
        props = new Properties();
        try {
            in = new FileInputStream(file);
            props.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    
    
    /**
     * Writes this configuration file back to the disc.
     * 
     * @throws IOException If storing the file fails for any reason.
     */
    public synchronized void store() throws IOException {
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
    
    
    
    protected boolean parseBoolean(String value) throws ConfigurationFileException {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new ConfigurationFileException("Invalid boolean literal.");
        }
    }
}
