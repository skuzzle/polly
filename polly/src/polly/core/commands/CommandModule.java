package polly.core.commands;

import polly.configuration.PollyConfiguration;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;


public class CommandModule extends AbstractPollyModule {

    PollyConfiguration config;
    
    public CommandModule(ModuleBlackboard initializer) {
        super("COMMAND", initializer, true);
    }

    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
    }
    
    
    
    @Override
    public boolean doSetup() throws Exception {
        CommandManagerImpl commandManager = new CommandManagerImpl(
            this.config.getIgnoredCommands());
        this.provideComponent(CommandManagerImpl.class, commandManager);
        return true;
    }
    
    
    public void doRun() throws Exception {}
}
