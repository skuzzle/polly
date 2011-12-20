package polly.core.telnet;

import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.core.irc.IrcManagerImpl;
import polly.eventhandler.MessageHandler;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;


public class TelnetModule extends AbstractPollyModule {

    private PollyConfiguration config;
    private IrcManagerImpl ircManager;
    private MessageHandler messageHandler;
    private ShutdownManagerImpl shutdownManager;
    private TelnetServer server;
    
    
    public TelnetModule(ModuleBlackboard initializer) {
        super("TELNET", initializer, false);
    }
    

    
    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
        this.ircManager = this.requireComponent(IrcManagerImpl.class);
        this.messageHandler = this.requireComponent(MessageHandler.class);
        this.shutdownManager = this.requireComponent(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public boolean doSetup() throws Exception {
        if (this.config.enableTelnet()) {
            this.server = new TelnetServer(
                this.config, this.ircManager, this.messageHandler);
            this.shutdownManager.addDisposable(this.server);
            
            this.provideComponent(TelnetServer.class, this.server);
        }
        
        return true;
    }
    
    

    @Override
    public void doRun() throws Exception {
        this.server.start();
    }

}
