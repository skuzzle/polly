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
        "http://nopaste.info/[0-9a-f]+\\.html");
    
    
    public NoPastePasteService() {
        super("nopaste");
    }
    
    

    @Override
    public String doPaste(String message) throws IOException {
        Map<String, String> properties = new HashMap<String, String>(10);
        

        properties.put("name", "polly");
        properties.put("code_lang", "Plain Text");
        properties.put("api_paste_name", "Polly Log output");
        properties.put("tab_length", "4");
        properties.put("description", "Polly Log");
        properties.put("code", message);
        properties.put("submit", "Add Entry");
        
        
        PostResult pr = this.postRequest(new URL("http://nopaste.info/index.html"), 
            properties);

        Matcher m = PATTERN.matcher(pr.getResultString());
        if (!m.find()) {
            throw new IOException("invalid result");
        }
        
        return pr.getResultString().substring(m.start(), m.end());
    }

}
