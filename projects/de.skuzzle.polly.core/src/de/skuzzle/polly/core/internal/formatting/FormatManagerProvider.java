package de.skuzzle.polly.core.internal.formatting;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;

@Module(
    requires = 
        @Require(component = ConfigurationProviderImpl.class),
    provides = 
        @Provide(component = FormatManagerImpl.class)
    )
public class FormatManagerProvider extends AbstractProvider {


    public FormatManagerProvider(ModuleLoader loader) {
        super("FORMAT_MANAGER_PROVIDER", loader, true); //$NON-NLS-1$
    }

    
    
    @Override
    public void setup() {
        ConfigurationProvider configProvider = 
            this.requireNow(ConfigurationProviderImpl.class, true);
        Configuration pollyCfg = configProvider.getRootConfiguration();
        FormatManagerImpl formatter = new FormatManagerImpl(pollyCfg);
        this.provideComponent(formatter);
    }

}
