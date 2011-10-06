package polly.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import de.skuzzle.polly.sdk.Version;


public class UpdateProperties {

    public final static String UPDATE_URL = "updateURL";
    public final static String UPDATE_NAME = "name";
    public final static String UPDATE_DESCRIPTION = "updateDescription";
    public final static String UPDATE_VERSION = "updateVersion";
    public final static String UPDATE_CHEKSUM = "checksum";
    
    
    private Properties props;
    private String name;
    private String description;
    private long checksum;
    private URL updateUrl;
    private Version updateVersion;
    
    
    public UpdateProperties(URL propertyUrl) throws IOException, UpdateException {
        this.props = new Properties();
        this.props.load(propertyUrl.openStream());
        this.validate();
    }
    
    
    
    public UpdateProperties(File local) throws IOException, UpdateException {
        this.props = new Properties();
        this.props.load(new FileInputStream(local));
        this.validate();
    }
    
    
    
    private void validate() throws UpdateException {
        try {
            this.name = this.props.getProperty(UPDATE_NAME);
            this.updateUrl = new URL(this.props.getProperty(UPDATE_URL));
            this.checksum = Long.parseLong(this.props.getProperty(UPDATE_CHEKSUM, "0"));
            this.updateVersion = new Version(this.props.getProperty(UPDATE_VERSION));
        } catch (MalformedURLException e) {
            throw new UpdateException(UPDATE_URL + ":" + 
                this.props.getProperty(UPDATE_URL), e);
        } catch (NumberFormatException e) {
            throw new UpdateException(UPDATE_CHEKSUM, e);
        } catch (IllegalArgumentException e) {
            throw new UpdateException(e.getMessage(), e);
        }
        
        this.description = this.props.getProperty(UPDATE_DESCRIPTION, "");
    }
    
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public long getChecksum() {
        return this.checksum;
    }
    
    
    
    public String getDescription() {
        return this.description;
    }
    
    
    
    public URL getUpdateUrl() {
        return this.updateUrl;
    }
    
    
    
    public Version getVersion() {
        return this.updateVersion;
    }
}