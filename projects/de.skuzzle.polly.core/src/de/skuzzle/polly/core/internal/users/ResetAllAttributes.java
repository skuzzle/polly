package de.skuzzle.polly.core.internal.users;



public class ResetAllAttributes implements Runnable {

    private final UserManagerImpl userManager;
    
    
    public ResetAllAttributes(UserManagerImpl userManager) {
        this.userManager = userManager;
    }
    
    
    
    @Override
    public void run() {
        this.userManager.resetAllAttributes();
    }
}
