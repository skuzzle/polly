package http;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;

public class SearchHttpAction extends HttpAction {

    
    public SearchHttpAction(MyPolly myPolly) {
        super("/Search", myPolly);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext c = new HttpTemplateContext("pages/search.html");
        
        return c;
    }

}
