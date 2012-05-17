package polly.core.http.actions;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class RootHttpAction extends HttpAction {

    private MyPolly myPolly;
    
    
    public RootHttpAction(MyPolly myPolly) {
        super("/");
        this.myPolly = myPolly;
    }

    
    
    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        context.setTemplate("webinterface/pages/home.html");
        context.put("formatter", this.myPolly.formatting());
        context.put("started", this.myPolly.getStartTime());
        long uptime = this.myPolly.currentTimeMillis() - this.myPolly.getStartTime().getTime();
        context.put("uptime", uptime / 1000);
    }
}
