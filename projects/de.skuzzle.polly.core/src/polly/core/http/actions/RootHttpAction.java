package polly.core.http.actions;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.time.Time;


public class RootHttpAction extends HttpAction {

    
    public RootHttpAction(MyPolly myPolly) {
        super("/", myPolly);
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_HOME);
        context.put("formatter", this.myPolly.formatting());
        context.put("started", this.myPolly.getStartTime());
        long uptime = Time.currentTimeMillis() - this.myPolly.getStartTime().getTime();
        context.put("uptime", uptime / 1000);
        return context;
    }
}
