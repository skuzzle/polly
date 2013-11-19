package de.skuzzle.polly.core.internal.paste.services;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.paste.AbstractPasteService;
import de.skuzzle.polly.sdk.paste.PostResult;


public class NoPastePasteService extends AbstractPasteService {
    
    private final static Pattern PATTERN = Pattern.compile(
        "http://nopaste.info/[0-9a-f]+\\.html"); //$NON-NLS-1$
    
    
    public NoPastePasteService() {
        super("nopaste"); //$NON-NLS-1$
    }
    
    

    @Override
    public String doPaste(String message) throws IOException {
        Map<String, String> properties = new HashMap<String, String>(10);
        

        properties.put("name", "polly"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("code_lang", "Plain Text"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("api_paste_name", "Polly Log output"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("tab_length", "4"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("description", "Polly Log"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("code", message); //$NON-NLS-1$
        properties.put("submit", "Add Entry"); //$NON-NLS-1$ //$NON-NLS-2$
        
        
        PostResult pr = this.postRequest(new URL("http://nopaste.info/index.html"),  //$NON-NLS-1$
            properties);

        Matcher m = PATTERN.matcher(pr.getResultString());
        if (!m.find()) {
            throw new IOException("invalid result"); //$NON-NLS-1$
        }
        
        return pr.getResultString().substring(m.start(), m.end());
    }

}
