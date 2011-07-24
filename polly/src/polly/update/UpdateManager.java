package polly.update;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.Disposable;



public class UpdateManager implements Disposable {

    public static void main(String[] args) {
        UpdateManager um = new UpdateManager();
        
        try {
            um.checkAndStart(0.0f, 
                "http://www.polly.skuzzle.de/release/pollyUpdate.properties", 
                "C:\\Users\\Simon\\Desktop\\temp\\");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static Logger logger = Logger.getLogger(UpdateManager.class.getName());
    public final static String UPDATE_URL = "updateURL";
    public final static String UPDATE_CRC = "updateCRC";
    public final static String UPDATE_VERSION = "updateVersion";
    
    
    private DownloadManager downloadManager;
    private List<UpdateItem> updates;
    
    
    public UpdateManager() {
        this.updates = new LinkedList<UpdateItem>();
        this.downloadManager = new DownloadManager();
    }

   
    
    public UpdateItem checkAndStart(float currentVersion, String propertyFile, 
            String tempPath) throws IOException {
        
        Properties props = this.readProperties(propertyFile);
        if (this.checkForUpdate(currentVersion, props)) {
            logger.info("Newer version is available.");
            URL updateUrl = new URL(props.getProperty(UPDATE_URL));

            File tempFile = File.createTempFile("polly", "", new File(tempPath));
            UpdateItem ui = new UpdateItem(props, tempFile);
            this.updates.add(ui);
            
            logger.info("Downloading update: " + ui);
            this.downloadManager.downloadLater(updateUrl, tempFile, ui);
            return ui;
        }
        return null;
    }
    
    
    
    public boolean checkForUpdate(float currentVersion, Properties props) {
        String versionString = props.getProperty(UPDATE_VERSION);
        float f = Float.parseFloat(versionString);
        return f > currentVersion;
    }
    
    
    
    private Properties readProperties(String location) throws IOException {
        logger.info("Getting update information from " + location);
        URL url = new URL(location);
        Properties props = new Properties();
        props.load(url.openStream());
        return props;
    }



    @Override
    public void dispose() {
        this.downloadManager.dispose();
    }
}