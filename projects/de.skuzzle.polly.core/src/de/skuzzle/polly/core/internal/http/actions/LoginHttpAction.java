package de.skuzzle.polly.core.internal.http.actions;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.internal.http.HttpInterface;
import de.skuzzle.polly.core.internal.http.HttpManagerProvider;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;



public class LoginHttpAction extends HttpAction {

    private final static Logger logger = Logger.getLogger(LoginHttpAction.class
        .getName());
    
    private UserManager userManager;
    
    
    
    public LoginHttpAction(MyPolly myPolly) {
        super("/login", myPolly);
        this.userManager = myPolly.users();
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) 
            throws HttpTemplateException, InsufficientRightsException {
        
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_LOGIN);
        String userName = e.getProperty("userName");
        String password = e.getProperty("password");

        
        if (userName != null && password != null) {
            context.put("noAction", Boolean.FALSE);
            User u = this.userManager.getUser(userName);

            if (u != null) {
                if (u.checkPassword(password)) {
                    e.getSession().setUser(u);
                    logger.info("Successfull http login: " + u);
                    
                    String homePage = ((StringType) 
                        u.getAttribute(HttpManagerProvider.HOME_PAGE)).getValue();
                    HttpEvent e1 = new HttpEvent(e.getSource(), e.getSession(), homePage);
                    HttpTemplateContext result = e.getSource().executeAction(e1);
                    return result;
                } else {
                    logger.warn("Invalid login attempt: " + userName);
                    e.throwTemplateException("Login Error", 
                        "Invalid login data");
                }
            } else {
                logger.warn("Invalid login attempt: " + userName);
                e.throwTemplateException("Login Error", 
                    "Invalid login data");
            }
        } else {
            context.put("noAction", Boolean.TRUE);
        }
        return context;
    }

}
