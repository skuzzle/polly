package de.skuzzle.polly.core.eventhandler;

import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;



public class TraceNickChangeHandler implements NickChangeListener {

    private UserManagerImpl userManager;
    
    
    public TraceNickChangeHandler(UserManagerImpl userManager) {
        this.userManager = userManager;
    }
    
    
    
    @Override
    public void nickChanged(NickChangeEvent e) {
        if (this.userManager.isSignedOn(e.getOldUser())) {
            this.userManager.traceNickChange(e.getOldUser(), e.getNewUser());
        }
    }

}
