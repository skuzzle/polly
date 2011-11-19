package polly.logging;


import core.IrcLogCollector;
import core.PollyLoggingManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import entities.LogEntry;



public abstract class MyPlugin extends PollyPlugin {

    
    private IrcLogCollector logCollector;
    private PollyLoggingManager logManager;
    
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
        super(myPolly);
        
        myPolly.persistence().registerEntity(LogEntry.class);
        
        this.logManager = new PollyLoggingManager(myPolly);
        this.logCollector = new IrcLogCollector(this.logManager);
        
        this.addDisposable(this.logManager);
        
        myPolly.irc().addJoinPartListener(this.logCollector);
        myPolly.irc().addMessageListener(this.logCollector);
        myPolly.irc().addQuitListener(this.logCollector);
        myPolly.irc().addNickChangeListener(this.logCollector);
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        
        this.getMyPolly().irc().removeJoinPartListener(this.logCollector);
        this.getMyPolly().irc().removeMessageListener(this.logCollector);
        this.getMyPolly().irc().removeQuitListener(this.logCollector);
        this.getMyPolly().irc().removeNickChangeListener(this.logCollector);
    }
    
    
    

}
