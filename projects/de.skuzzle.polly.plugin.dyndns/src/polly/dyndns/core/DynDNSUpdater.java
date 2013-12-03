package polly.dyndns.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import polly.dyndns.entities.Hoster;
import sun.misc.BASE64Encoder;
import de.skuzzle.polly.sdk.time.Time;


public class DynDNSUpdater implements IPChangedListener {

    private final Logger logger;
    private final HostManager manager;
    
    
    
    public DynDNSUpdater(String loggerName, HostManager manager) {
        this.logger = Logger.getLogger(loggerName);
        this.manager = manager;
    }
    
    
    
    @Override
    public void ipChanged(IPChangedEvent e) {
        for (final Hoster hoster : this.manager.getAllHosters()) {
            this.performUpdate(hoster, e);
        }
    }
    
    
    
    private void performUpdate(Hoster hoster, IPChangedEvent event) {
        logger.info("Updating DynDNS host: " + hoster.getHostName());
        String updateUrl = hoster.getApiUrl();
        updateUrl = updateUrl.replace("<hostname>", hoster.getHostName());
        updateUrl = updateUrl.replace("<username>", hoster.getUserName());
        updateUrl = updateUrl.replace("<pass>", hoster.getPassword());
        updateUrl = updateUrl.replaceAll("<ip>", event.getCurrentIp());
        
        final BASE64Encoder encoder = new BASE64Encoder();
        final String authorization = encoder.encode((hoster.getUserName() + 
                ":" + hoster.getPassword()).getBytes());
        
        try {
            final URL url = new URL(updateUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Polly DynDNS Client");
            connection.setRequestProperty("Authorization", "Basic " + authorization);
            
            int code = connection.getResponseCode();
            logger.trace("Hoster Response Code: " + code);
            
            try (final BufferedReader r = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line = null;
                final StringBuilder b = new StringBuilder();
                while ((line = r.readLine()) != null) {
                    b.append(line);
                    b.append("\n");
                }
                logger.info("Response: " + b);
                hoster.setCurrentStatus(b.toString());
                hoster.setUpdateTime(Time.currentTimeMillis());
            }
        } catch (Exception e) {
            logger.error("Error while updating the host", e);
        }
    }
}
