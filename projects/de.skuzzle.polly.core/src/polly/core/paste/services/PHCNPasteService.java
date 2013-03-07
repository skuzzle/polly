package polly.core.paste.services;

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
            "http://paste.phcn.ws/\\?i=[0-9]+");
    

    public PHCNPasteService() {
        super("phcn");
    }
    
    

    @Override
    public String doPaste(String message) throws IOException {
        Map<String, String> properties = new HashMap<String, String>(10);
        
        properties.put("language", "");
        properties.put("blppassword", "");
        properties.put("input", message);
        
        PostResult pr = this.postRequest(new URL("http://paste.phcn.ws/#"), properties);
        
        Matcher m = PATTERN.matcher(pr.getResultString());
        if (!m.find()) {
            throw new IOException("invalid result");
        }
        
        return pr.getResultString().substring(m.start(), m.end());
    }

}
