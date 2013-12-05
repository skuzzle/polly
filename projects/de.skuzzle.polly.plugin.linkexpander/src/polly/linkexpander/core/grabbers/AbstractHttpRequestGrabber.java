package polly.linkexpander.core.grabbers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;



public abstract class AbstractHttpRequestGrabber implements LinkGrabber {

    private String findCharset(String contentType) {
        for (String param : contentType.replace(" ", "").split(";")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if (param.startsWith("charset=")) { //$NON-NLS-1$
                return param.split("=", 2)[1]; //$NON-NLS-1$
            }
        }
        return "UTF-8"; //$NON-NLS-1$
    }
    
    
    
    @Override
    public String processMatch(MessageEvent matched, Matcher matcher) {
        String grabbed = matched.getMessage();
        URLConnection c = null;
        BufferedReader r = null;
        try {
            final URL url = new URL(this.getLink(grabbed, matcher));
            c = url.openConnection();
            c.setDoInput(true);
            
            final String charset = this.findCharset(c.getHeaderField("Content-Type")); //$NON-NLS-1$
            r = new BufferedReader(new InputStreamReader(c.getInputStream(), charset));
            String line = null;
            while ((line = r.readLine()) != null) {
                final String processed = this.processResponseLine(
                        HTMLTools.unescape(line));
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