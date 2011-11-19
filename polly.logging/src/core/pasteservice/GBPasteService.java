package core.pasteservice;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class GBPasteService extends AbstractPasteService {
    

    public GBPasteService() {
        super("gbpaste");
    }
    
    

    @Override
    public String paste(String message) throws IOException {
        Map<String, String> properties = new HashMap<String, String>(10);
        
        properties.put("nick", "polly");
        properties.put("lang", "text");
        properties.put("private", "1");
        properties.put("desc", "polly log entries");
        properties.put("code", message);
        
        return this.postRequest(
                new URL("http://gbpaste.org/upload"), properties).getResultURL();
    }

}
