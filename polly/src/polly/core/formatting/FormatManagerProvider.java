package polly.core.formatting;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import polly.configuration.ConfigurationProviderImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = 
        @Require(component = ConfigurationProviderImpl.class),
    provides = 
        @Provide(component = FormatManagerImpl.class)
    )
public class FormatManagerProvider extends AbstractModule {


    public FormatManagerProvider(ModuleLoader loader) {
        super("FORMAT_MANAGER_PROVIDER", loader, true);
    }

    
    
    @Override
    public void setup() {
        ConfigurationProvider configProvider = 
            this.requireNow(ConfigurationProviderImpl.class);
        Configuration pollyCfg = configProvider.getRootConfiguration();
        FormatManagerImpl formatter = new FormatManagerImpl(pollyCfg);
        this.provideComponent(formatter);
    }

}
