package polly.core.commands;

import polly.configuration.PollyConfiguration;
import polly.core.users.UserManagerImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = {
               @Require(component = PollyConfiguration.class),
               @Require(component = UserManagerImpl.class)},
    provides = @Provide(component = CommandManagerImpl.class))
public class CommandManagerProvider extends AbstractModule {

    PollyConfiguration config;



    public CommandManagerProvider(ModuleLoader loader) {
        super("COMMAND_MANAGER_PROVIDER", loader, true);
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
    
    
    
    
    @Override
    public void dispose() {
        this.config = null;
        super.dispose();
    }
}
