package polly.core.mail;

import org.apache.log4j.Logger;

import polly.core.mail.senders.MailSender;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;


@Module(requires = @Require(component = MailConfig.class))
public class ErrorMailerProvider extends AbstractModule {

    
    public ErrorMailerProvider(ModuleLoader loader) {
        super("ERROR_MAILER_PROVIDER", loader, false);
    }
    
    

    @Override
    public void setup() throws SetupException {
        MailConfig mailCfg = this.requireNow(MailConfig.class);
        int delay = Integer.parseInt(
            mailCfg.readString(MailConfig.MAIL_DELAY));
        MailSender sender = mailCfg.getSender();
        EMailLogAppender appender = new EMailLogAppender(
            sender, mailCfg.getLevel(), delay, 
            new EMailLogFormatter(mailCfg.getLevel()));
        
        Logger.getRootLogger().addAppender(appender);
    }

}
