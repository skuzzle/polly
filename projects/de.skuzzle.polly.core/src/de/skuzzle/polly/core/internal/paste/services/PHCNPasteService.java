package de.skuzzle.polly.core.internal.paste.services;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.paste.AbstractPasteService;
import de.skuzzle.polly.sdk.paste.PostResult;


public class PHCNPasteService extends AbstractPasteService {
    
    private final static Pattern PATTERN = Pattern.compile(
            "http://paste.phcn.ws/\\?i=[0-9]+"); //$NON-NLS-1$
    

    public PHCNPasteService() {
        super("phcn"); //$NON-NLS-1$
    }
    
    

    @Override
    public String doPaste(String message) throws IOException {
        Map<String, String> properties = new HashMap<String, String>(10);
        
        properties.put("language", ""); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("blppassword", ""); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("input", message); //$NON-NLS-1$
        
        PostResult pr = this.postRequest(new URL("http://paste.phcn.ws/#"), properties); //$NON-NLS-1$
        
        Matcher m = PATTERN.matcher(pr.getResultString());
        if (!m.find()) {
            throw new IOException("invalid result"); //$NON-NLS-1$
        }
        
        return pr.getResultString().substring(m.start(), m.end());
    }

}
