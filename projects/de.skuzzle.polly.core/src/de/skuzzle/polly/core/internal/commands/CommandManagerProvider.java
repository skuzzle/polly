package de.skuzzle.polly.core.internal.commands;

import java.io.IOException;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;

@Module(
    requires = {
       @Require(component = ConfigurationProviderImpl.class),
       @Require(component = ShutdownManagerImpl.class)
    },
    provides = @Provide(component = CommandManagerImpl.class))
public class CommandManagerProvider extends AbstractProvider {

    
    private final static String COMMAND_CFG_NAME = "commandManager.cfg";


    public CommandManagerProvider(ModuleLoader loader) {
        super("COMMAND_MANAGER_PROVIDER", loader, true);
    }



    @Override
    public void setup() throws SetupException {
        ConfigurationProvider configProvider = 
                this.requireNow(ConfigurationProviderImpl.class, true);
        Configuration commandCfg = null;
        Configuration pollyCfg = null;
        try {
            commandCfg = configProvider.open(COMMAND_CFG_NAME);
            pollyCfg = configProvider.getRootConfiguration();
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        CommandManagerImpl commandManager = 
                new CommandManagerImpl(
                pollyCfg.readString(Configuration.ENCODING),
                commandCfg);
        
        final ShutdownManagerImpl shutDownMngr = this.requireNow(
                ShutdownManagerImpl.class, true);
        
        shutDownMngr.addDisposable(commandManager);
        this.provideComponent(commandManager);
    }
}
