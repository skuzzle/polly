package de.skuzzle.polly.sdk.resources;

import java.util.Locale;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Configuration;

@Module(
    requires = @Require(component = ConfigurationProviderImpl.class),
    provides = @Provide(component = Locale.class)
)
public class LocaleProvider extends AbstractProvider {

    private final static Logger logger = Logger.getLogger(LocaleProvider.class.getName());
    
    public LocaleProvider(ModuleLoader loader) {
        super("LOCALE_PROVIDER", loader, true);
    }

    
    
    @Override
    public void setup() throws SetupException {
        final ConfigurationProviderImpl cfgProvider = 
                this.requireNow(ConfigurationProviderImpl.class, true);
        
        final Configuration pollyCfg = cfgProvider.getRootConfiguration();
        final String localeName = pollyCfg.readString(Configuration.LOCALE);
        
        if (localeName == null) {
            throw new SetupException("No locale set in polly configuration");
        }
        logger.info("Using locale: '" + localeName + "'");
        
        final Locale locale = new Locale(localeName);
        Resources.pollyLocale = locale;
        this.provideComponentAs(Locale.class, locale);
    }

}
