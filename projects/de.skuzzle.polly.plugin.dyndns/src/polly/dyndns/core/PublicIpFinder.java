package polly.dyndns.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.EventProviders;


public class PublicIpFinder {

    private final static String API_URL = "http://www.skuzzle.de/ip.php"; //$NON-NLS-1$
    
    private volatile String lastKnownIp;
    private volatile long lastUpdate;
    
    private final ScheduledExecutorService updater;
    private final EventProvider eventProvider;
    private final Logger logger;
    
    
    
    private final class UpdateRunnable implements Runnable {
        
        private final boolean forceUpdate;
        
        
        
        public UpdateRunnable(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }
        
        
        
        @Override
        public void run() {
            logger.info("Obtaining new IP from '" + API_URL + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            try {
                final URL url = new URL(API_URL);
                
                final URLConnection connection = url.openConnection();
                try (BufferedReader r = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    
                    final String line = r.readLine();
                    synchronized (PublicIpFinder.this) {
                        if (this.forceUpdate || line != null && !line.equals(lastKnownIp)) {
                            logger.info("Public IP has changed from " +  //$NON-NLS-1$
                                    lastKnownIp + " to " + line + ", forced: " +  //$NON-NLS-1$ //$NON-NLS-2$
                                    this.forceUpdate); 
                            final IPChangedEvent e = new IPChangedEvent(
                                    PublicIpFinder.this, line);
                            fireIPChangedEvent(e);
                            lastKnownIp = line;
                            lastUpdate = Time.currentTimeMillis();    
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error while retrieving current public API", e); //$NON-NLS-1$
                e.printStackTrace();
            }
        }
    }
    
    
    
    /**
     * 
     * @param loggerName Name of the logger
     * @param updateInterval Interval in which this class checks for IP changes 
     *          (in minutes). 
     */
    public PublicIpFinder(String loggerName, int updateInterval) {
        this.logger = Logger.getLogger(loggerName);
        this.eventProvider = EventProviders.newDefaultEventProvider();
        this.updater = Executors.newScheduledThreadPool(1, 
                new ThreadFactoryBuilder("DynDNS_Service").setDaemon(false)); //$NON-NLS-1$
        
        this.updater.scheduleAtFixedRate(new UpdateRunnable(false), updateInterval, 
                updateInterval, TimeUnit.MINUTES);
    }
    
    
    
    public String getLastKnownIp() {
        return this.lastKnownIp;
    }
    
    
    
    public long getLastUpdate() {
        return this.lastUpdate;
    }
    
    
    
    public void updateNow() {
        new UpdateRunnable(true).run();
    }
    
    
    
    public void addIPChangedListener(IPChangedListener listener) {
        this.eventProvider.addListener(IPChangedListener.class, listener);
    }
    
    
    
    public void removeIPChangedListener(IPChangedListener listener) {
        this.eventProvider.addListener(IPChangedListener.class, listener);
    }
    
    
    
    protected void fireIPChangedEvent(IPChangedEvent e) {
        this.eventProvider.dispatch(IPChangedListener.class, e, 
                IPChangedListener.IP_CHANGED);
    }
}
