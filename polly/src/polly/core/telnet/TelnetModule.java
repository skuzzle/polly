package polly.core.telnet;

import java.io.IOException;
import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.ModuleStates;
import polly.core.SetupException;
import polly.core.ShutdownManagerImpl;
import polly.core.irc.IrcManagerImpl;
import polly.eventhandler.MessageHandler;


public class TelnetModule extends AbstractModule {

    private PollyConfiguration config;
    private IrcManagerImpl ircManager;
    private MessageHandler messageHandler;
    private ShutdownManagerImpl shutdownManager;
    private TelnetServer server;
    
    
    public TelnetModule(ModuleLoader loader) {
        super("TELNET", loader, false);
        
        this.requireBeforeSetup(PollyConfiguration.class);
        this.requireBeforeSetup(IrcManagerImpl.class);
        this.requireBeforeSetup(MessageHandler.class);
        this.requireBeforeSetup(ShutdownManagerImpl.class);
        
        this.willProvideDuringSetup(TelnetServer.class);
        
        this.willSetState(ModuleStates.TELENT_READY);
    }
    

    
    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.ircManager = this.requireNow(IrcManagerImpl.class);
        this.messageHandler = this.requireNow(MessageHandler.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public void setup() throws SetupException {
        if (this.config.enableTelnet()) {
            try {
                this.server = new TelnetServer(
                    this.config, this.ircManager, this.messageHandler);
                
                this.shutdownManager.addDisposable(this.server);
                
                this.provideComponent(this.server);
            } catch (IOException e) {
                throw new SetupException(e);
            } 
        }
    }
    
    


    public void run() throws Exception {
        this.server.start();
        this.addState(ModuleStates.TELENT_READY);
    }

}
