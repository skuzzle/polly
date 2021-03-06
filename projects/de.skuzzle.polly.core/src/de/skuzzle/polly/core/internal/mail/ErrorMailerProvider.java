package de.skuzzle.polly.core.internal.mail;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.mail.senders.MailSender;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Configuration;



@Module(requires = {
    @Require(component = MailConfig.class),
    @Require(component = ConfigurationProviderImpl.class)
})
public class ErrorMailerProvider extends AbstractProvider {

    private final static Logger logger = Logger
        .getLogger(ErrorMailerProvider.class.getName());
    
    public ErrorMailerProvider(ModuleLoader loader) {
        super("ERROR_MAILER_PROVIDER", loader, false);
    }
    
    

    @Override
    public void setup() throws SetupException {
        MailConfig mailCfg = this.requireNow(MailConfig.class, true);
        ConfigurationProviderImpl configManager = 
                this.requireNow(ConfigurationProviderImpl.class, true);
        
        if (configManager.getRootConfiguration().readBoolean(Configuration.DEBUG_MODE)) {
            logger.warn("Disabling Error Mailer because polly debug mode is enabled");
            return;
        }
        
        int delay = Integer.parseInt(
            mailCfg.readString(MailConfig.MAIL_DELAY));
        MailSender sender = mailCfg.getSender();
        EMailLogAppender appender = new EMailLogAppender(
            sender, mailCfg.getLevel(), delay, 
            new EMailLogFormatter(mailCfg.getLevel()));
        
        Logger.getRootLogger().addAppender(appender);
    }

}
