package polly.linkexpander.core.grabbers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;



public abstract class AbstractHttpRequestGrabber implements LinkGrabber {

    
    public String processMatch(String grabbed, Matcher matcher) {
        
        URLConnection c = null;
        BufferedReader r = null;
        try {
            URL url = new URL(this.getLink(grabbed, matcher));
            c = url.openConnection();
            c.setDoInput(true);
            
            r = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String line = null;
            while ((line = r.readLine()) != null) {
                String processed = this.processResponseLine(line);
                if (processed != null) {
                    return processed;
                }
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return null;
    }
    
    
    
    public abstract String processResponseLine(String line);
}