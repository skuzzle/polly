package de.skuzzle.polly.core.internal.paste.services;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.sdk.paste.AbstractPasteService;


public class GBPasteService extends AbstractPasteService {
    

    public GBPasteService() {
        super("gbpaste"); //$NON-NLS-1$
    }
    
    

    @Override
    public String doPaste(String message) throws IOException {
        Map<String, String> properties = new HashMap<String, String>(10);
        
        properties.put("nick", "polly"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("lang", "text"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("private", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("desc", "polly log entries"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("code", message); //$NON-NLS-1$
        
        return this.postRequest(
                new URL("http://gbpaste.org/upload"), properties).getResultURL(); //$NON-NLS-1$
    }

}
