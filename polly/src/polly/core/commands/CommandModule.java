package polly.core.commands;

import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.annotation.Module;
import polly.core.annotation.Provide;
import polly.core.annotation.Require;
import polly.core.users.UserManagerImpl;


@Module(
    requires = {
               @Require(component = PollyConfiguration.class),
               @Require(component = UserManagerImpl.class)},
    provides = @Provide(component = CommandManagerImpl.class))
public class CommandModule extends AbstractModule {

    PollyConfiguration config;



    public CommandModule(ModuleLoader loader) {
        super("MODULE_COMMAND", loader, true);
    }



    @Override
    public void beforeSetup() {
        super.beforeSetup();
    }



    @Override
    public void setup() {
        PollyConfiguration config = this.requireNow(PollyConfiguration.class);
        UserManagerImpl userManager = this.requireNow(UserManagerImpl.class);
        
        CommandManagerImpl commandManager = new CommandManagerImpl(userManager, config);
        this.provideComponent(commandManager);
    }
}
