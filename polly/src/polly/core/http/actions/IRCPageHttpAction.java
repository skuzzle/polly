package polly.core.http.actions;


import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class IRCPageHttpAction extends HttpAction {

    private MyPolly myPolly;
    
    
    public IRCPageHttpAction(MyPolly myPolly) {
        super("/IRC");
        this.myPolly = myPolly;
    }
    
    
    
    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        context.setTemplate("webinterface/pages/irc.html");
        context.put("formatter", this.myPolly.formatting());
        if (e.getSession().isLoggedIn()) {
            context.put("channels", this.myPolly.irc().getChannels());
        }
    }

}
