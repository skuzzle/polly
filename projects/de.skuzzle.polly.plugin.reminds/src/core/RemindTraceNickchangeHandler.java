package core;


import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;

public class RemindTraceNickchangeHandler implements NickChangeListener {

    private RemindManager remindManager;
    
    public RemindTraceNickchangeHandler(RemindManager manager) {
        this.remindManager = manager;
    }
    
    
    @Override
    public void nickChanged(NickChangeEvent e) {
        this.remindManager.traceNickChange(e.getOldUser(), e.getNewUser());
    }
}
