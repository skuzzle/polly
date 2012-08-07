package http;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;

public class LoggingHttpAction extends HttpAction {

    
    public LoggingHttpAction(MyPolly myPolly) {
        super("/Logging", myPolly);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext c = new HttpTemplateContext("pages/logging.html");
        
        return c;
    }

}
