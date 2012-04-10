package core;


import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;

public class RemindTraceNickchangeHandler implements NickChangeListener {

    private RemindManagerImpl remindManagerImpl;
    
    public RemindTraceNickchangeHandler(RemindManagerImpl manager) {
        this.remindManagerImpl = manager;
    }
    
    
    @Override
    public void nickChanged(NickChangeEvent e) {
        this.remindManagerImpl.traceNickChange(e.getOldUser(), e.getNewUser());
    }
}
