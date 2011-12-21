package polly.core.commands;

import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;

public class CommandModule extends AbstractModule {

    PollyConfiguration config;



    public CommandModule(ModuleLoader loader) {
        super("MODULE_COMMAND", loader, true);
        this.requireBeforeSetup(PollyConfiguration.class);
        this.willProvideDuringSetup(CommandManagerImpl.class);
    }



    @Override
    public void beforeSetup() {
        super.beforeSetup();
    }



    @Override
    public void setup() {
        PollyConfiguration config = this.requireNow(PollyConfiguration.class);
        CommandManagerImpl commandManager = new CommandManagerImpl(
            config.getIgnoredCommands());
        this.provideComponent(commandManager);
    }
}
