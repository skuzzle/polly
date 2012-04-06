package polly.core.mail;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;


@Module
public class ErrorMailerModule extends AbstractModule {

    public ErrorMailerModule(ModuleLoader loader) {
        super("MODULE_ERROR_MAILER", loader, false);
    }


    private MailConfig config;
    
    
    @Override
    public void setup() throws SetupException {
        try {
            this.config = new MailConfig("cfg/mail.cfg");
        } catch (Exception e) {
            throw new SetupException(e);
        }
        
        MailSender sender = new MailSender(this.config);
        EMailLogAppender appender = new EMailLogAppender(sender, 
            Level.toLevel(this.config.getProperty(MailConfig.LOG_THRESHOLD)));
        
        Logger.getRootLogger().addAppender(appender);
    }

    
}