package polly.dyndns.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import polly.dyndns.MSG;
import polly.dyndns.entities.Account;
import polly.dyndns.entities.Hoster;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import de.skuzzle.polly.sdk.time.Time;


public class DynDNSUpdater implements IPChangedListener {

    private final static Pattern BASE_64_TAG = Pattern.compile("<b64>(.*)</b64>"); //$NON-NLS-1$
    private final static BASE64Encoder ENCODER = new BASE64Encoder();
    
    
    private final Logger logger;
    private final HostManager manager;
    
    
    
    public DynDNSUpdater(String loggerName, HostManager manager) {
        this.logger = Logger.getLogger(loggerName);
        this.manager = manager;
    }
    
    
    
    @Override
    public void ipChanged(IPChangedEvent e) {
        for (final Account account : this.manager.getAllAccounts()) {
            this.performUpdate(account, e);
        }
    }
    
    
    
    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
    
    
    
    private void performUpdate(Account account, IPChangedEvent event) {
        final Hoster hoster = account.getHoster();
        logger.info("Updating DynDNS host: " + hoster.getName() + ": " + account.getDomainName()); //$NON-NLS-1$ //$NON-NLS-2$
        
        String updateUrl = hoster.getBaseUrl();
        updateUrl = updateUrl.replace("<domain>", this.urlEncode(account.getDomainName())); //$NON-NLS-1$
        updateUrl = updateUrl.replace("<username>", this.urlEncode(account.getUserName())); //$NON-NLS-1$
        updateUrl = updateUrl.replace("<pass>", this.urlEncode(account.getPassword())); //$NON-NLS-1$
        updateUrl = updateUrl.replaceAll("<ip>", this.urlEncode(event.getCurrentIp())); //$NON-NLS-1$
        
        final Matcher m = BASE_64_TAG.matcher(updateUrl);
        final StringBuffer buff = new StringBuffer();
        while (m.find()) {
            final String encoded = ENCODER.encode(m.group(1).getBytes());
            m.appendReplacement(buff, this.urlEncode(encoded));
        }
        m.appendTail(buff);
        updateUrl = buff.toString();

        final String authorization = ENCODER.encode((account.getUserName() + 
                ":" + account.getPassword()).getBytes()); //$NON-NLS-1$
        
        try {
            final URL url = new URI(updateUrl).toURL();
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); //$NON-NLS-1$
            connection.setRequestProperty("User-Agent", "Polly DynDNS Client"); //$NON-NLS-1$ //$NON-NLS-2$
            connection.setRequestProperty("Authorization", "Basic " + authorization); //$NON-NLS-1$ //$NON-NLS-2$
            
            int code = connection.getResponseCode();
            logger.trace("Hoster Response: " + code + " " + connection.getResponseMessage()); //$NON-NLS-1$
            
            try (final BufferedReader r = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line = null;
                final StringBuilder b = new StringBuilder();
                while ((line = r.readLine()) != null) {
                    b.append(line);
                    b.append("\n"); //$NON-NLS-1$
                }
                logger.info("Response: " + b); //$NON-NLS-1$
                account.setCurrentStatus(b.toString());
                account.setUpdateTime(Time.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while updating the host", e); //$NON-NLS-1$
            account.setCurrentStatus(MSG.bind(MSG.updateError, e.getMessage()));
            account.setUpdateTime(Time.currentTimeMillis());
        }
    }
}
