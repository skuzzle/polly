package polly.core.paste.services;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.sdk.paste.AbstractPasteService;


public class PasteBinPasteService extends AbstractPasteService {
    

    public PasteBinPasteService() {
        super("pastebin");
    }
    
    

    @Override
    public String doPaste(String message) throws IOException {
        Map<String, String> properties = new HashMap<String, String>(10);
        
        
        properties.put("api_dev_key", "48594e44d20ff92e3f132034b32397ef");
        /*properties.put("api_user_name", "polly123");
        properties.put("api_user_password", "polly123");*/
        properties.put("api_option", "paste");
        properties.put("api_paste_code", message);
        properties.put("api_paste_name", "Polly Log output");
        properties.put("api_paste_private", "1");
        
        
        return this.postRequest(
                new URL("http://pastebin.com/api/api_post.php"), properties).getResultString();
    }

}
