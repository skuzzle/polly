package de.skuzzle.polly.core.internal.runonce;

import java.io.IOException;

import de.skuzzle.polly.core.configuration.ConfigurationImpl;
import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;

@Module(
    requires = {
        @Require(component = ConfigurationProviderImpl.class),
        @Require(state = ModuleStates.USERS_READY),
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.PLUGINS_READY)
    },
    provides = @Provide(component = RunOnceManagerImpl.class)
)
public class RunOnceProvider extends AbstractProvider {

    public final static String RUN_ONCE_CONFIG = "runOnce.cfg";
    
    private RunOnceManagerImpl runOnceManager;
    private ConfigurationImpl runOnceCfg;
    
    
    public RunOnceProvider(ModuleLoader loader) {
        super("RUNONCE_PROVIDER", loader, false);
    }
    
    

    @Override
    public void setup() throws SetupException {
        final ConfigurationProviderImpl cfgProvider = this.requireNow(
            ConfigurationProviderImpl.class, true);
        
        try {
            this.runOnceCfg = (ConfigurationImpl) 
                cfgProvider.createConfiguration(RUN_ONCE_CONFIG);
            
            this.runOnceManager = new RunOnceManagerImpl(this.runOnceCfg);
            this.provideComponent(this.runOnceManager);
        } catch (IOException e) {
            throw new SetupException(e);
        }
    }
    
    
    
    @Override
    public void run() throws Exception {
        this.runOnceManager.runActions();
        this.runOnceCfg.store();
    }
    
    
    
    @Override
    public void dispose() {
        this.runOnceManager = null;
        super.dispose();
    }
}
