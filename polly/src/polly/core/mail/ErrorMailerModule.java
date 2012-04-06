package polly.core.mail;


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
        
        int delay = Integer.parseInt(this.config.getProperty(MailConfig.MAIL_DELAY));
        MailSender sender = this.config.getSender();
        EMailLogAppender appender = new EMailLogAppender(
            sender, this.config.getLevel(), delay);
        
        Logger.getRootLogger().addAppender(appender);
    }

    
}