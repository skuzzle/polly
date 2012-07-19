package polly.core.mail;

import org.apache.log4j.Logger;

import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;


@Module(requires = @Require(component = MailConfig.class))
public class ErrorMailerProvider extends AbstractModule {

    private MailConfig config;
    
    public ErrorMailerProvider(ModuleLoader loader) {
        super("ERROR_MAILER_PROVIDER", loader, false);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.config = this.requireNow(MailConfig.class);
    }
    
    

    @Override
    public void setup() throws SetupException {
        int delay = Integer.parseInt(this.config.getProperty(MailConfig.MAIL_DELAY));
        MailSender sender = this.config.getSender();
        EMailLogAppender appender = new EMailLogAppender(
            sender, this.config.getLevel(), delay, 
            new EMailLogFormatter(this.config.getLevel()));
        
        Logger.getRootLogger().addAppender(appender);
    }

}
