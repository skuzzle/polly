package polly.core.http.actions;


import java.net.URLDecoder;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.AbstractHttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.model.User;


public class UserInfoPageHttpAction extends AbstractHttpAction {

    private MyPolly myPolly;
    
    
    public UserInfoPageHttpAction(MyPolly myPolly) {
        super("/user_info");
        this.myPolly = myPolly;
    }
    
    
    
    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        context.setTemplate("webinterface/pages/user_info.html");
        context.put("formatter", this.myPolly.formatting());
        String userName = e.getProperty("userName");
        String action = e.getProperty("action");
        
        User u = this.myPolly.users().getUser(userName);
        context.put("user", u);
        context.put("action", action);
        
        if (action != null && action.equals("update")) {
            for (String attribute : u.getAttributeNames()) {
                try {
                    String value = URLDecoder.decode(e.getProperty(attribute), "UTF-8");
                    this.myPolly.users().setAttributeFor(u, attribute, value);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
