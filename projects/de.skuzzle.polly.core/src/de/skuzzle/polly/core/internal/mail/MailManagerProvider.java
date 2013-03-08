package de.skuzzle.polly.core.internal.mail;

import de.skuzzle.polly.core.configuration.ConfigurationImpl;
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
    provides = {
        @Provide(component = MailManagerImpl.class),
        @Provide(component = MailConfig.class)})
public class MailManagerProvider extends AbstractProvider {

    public final static String MAIL_CONFIG = "mail.cfg";
    

    
    public final static String RECIPIENTS = "recipients";
    public final static String LOG_THRESHOLD = "threshold";
    public final static String MAIL_PROVIDER = "mailProvider";

    
    
    
    public MailManagerProvider(ModuleLoader loader) {
        super("MAIL_MANAGER_PROVIDER", loader, false);
    }


    
    @Override
    public void setup() throws SetupException {
        ConfigurationProviderImpl configProvider = 
            this.requireNow(ConfigurationProviderImpl.class, true);
        Configuration config = null;
        try {
            config = configProvider.open(MAIL_CONFIG);
        } catch (Exception e) {
            throw new SetupException(e);
        }
        
        // HACK: MailConfig is a total hack currently
        MailConfig cfg = new MailConfig(
            (ConfigurationImpl) config,
            configProvider,
            config.readString(RECIPIENTS), 
            config.readString(LOG_THRESHOLD), 
            config.readString(MAIL_PROVIDER));
        
        
        this.provideComponent(cfg);
        MailManagerImpl mailManager = new MailManagerImpl(cfg.getSender());
        
        this.provideComponent(mailManager);
    }

    
}