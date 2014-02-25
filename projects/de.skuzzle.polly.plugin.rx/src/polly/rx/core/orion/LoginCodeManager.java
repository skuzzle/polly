package polly.rx.core.orion;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.time.Time;


public class LoginCodeManager {
    
    private final Pattern CODE_PATTREN = Pattern.compile("[a-z0-9]",  //$NON-NLS-1$
            Pattern.CASE_INSENSITIVE);
    
    private final static String EMPTY_HASH = "d41d8cd98f00b204e9800998ecf8427e"; //$NON-NLS-1$
    private final static String CAPTCHA_URL = "http://www.revorix.info/gfx/code/code.png"; //$NON-NLS-1$
    
    private LoginCode currentCode;
    
    
    
    public boolean testCodeValid(String code) {
        return code.length() == 4 && CODE_PATTREN.matcher(code).matches();
    }
    
    
    
    public synchronized boolean updateCurrentCode(String loginCode) {
        if (testCodeValid(loginCode)) {
            return false;
        }
        final String imgHash = this.getLoginHashCode();
        this.currentCode = new LoginCode(Time.currentTime(), loginCode, imgHash);
        return true;
    }
    
    
    
    public synchronized LoginCode getCurrentCode() {
        final LoginCode empty = new LoginCode(Time.currentTime(), "", EMPTY_HASH); //$NON-NLS-1$
        final String imgHash = this.getLoginHashCode();
        if (this.currentCode != null && !this.currentCode.getImgHash().equals(imgHash)) {
            // current code is outdated
            return empty; 
        } else if (this.currentCode != null) {
            return this.currentCode;
        }
        return empty; 
    }
    
    
    
    
    
    
    private String getLoginHashCode() {
        HttpURLConnection connection = null;
        try {
            final MessageDigest m = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
            final URL url = new URL(CAPTCHA_URL);
            connection = (HttpURLConnection) url.openConnection();
            final byte[] buffer = new byte[1024];
            
            try (final InputStream s = connection.getInputStream()) {
                int read = 0;
                while ((read = s.read(buffer)) != -1) {
                    m.update(buffer, 0, read);
                }
            }
            return new BigInteger(1, m.digest()).toString(16).toLowerCase();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return EMPTY_HASH;
    }
}
