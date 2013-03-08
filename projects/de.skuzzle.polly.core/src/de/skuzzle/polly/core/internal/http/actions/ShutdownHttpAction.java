package de.skuzzle.polly.core.internal.http.actions;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class ShutdownHttpAction extends HttpAction {

    public static long SHUTDOWN_DELAY = 5000; // 5sec
    
    
    
    public ShutdownHttpAction(MyPolly myPolly) {
        super("/shutdown", myPolly);
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException, 
            InsufficientRightsException {
        
        String password = e.getProperty("pw");
        
        UserManager userManager = this.getMyPolly().users();
        User user = userManager.getAdmin();
        
        if (password != null && user.checkPassword(password)) {
            Thread waitABit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(SHUTDOWN_DELAY);
                    } catch (InterruptedException ignore) {
                        
                    } finally {
                        getMyPolly().shutdownManager().shutdown();
                    }
                }
            });
            waitABit.start();
            e.throwTemplateException("Shutdown initiated!", "Byebye");
        }
        
        e.throwTemplateException("Shutdown denied!", "");
        return null; // not reachable!
    }

}
