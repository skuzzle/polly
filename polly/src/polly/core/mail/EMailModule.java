package polly.core.mail;


import org.apache.log4j.Logger;


import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;


@Module(provides = @Provide(component = MailManagerImpl.class))
public class EMailModule extends AbstractModule {

    
    
    public EMailModule(ModuleLoader loader) {
        super("MODULE_MAIL", loader, false);
    }


    private MailConfig config;
    
    
    
    @Override
    public void setup() throws SetupException {
        try {
            this.config = new MailConfig("cfg/mail.cfg");
        } catch (Exception e) {
            throw new SetupException(e);
        }
        
        // set up error mailer
        int delay = Integer.parseInt(this.config.getProperty(MailConfig.MAIL_DELAY));
        MailSender sender = this.config.getSender();
        EMailLogAppender appender = new EMailLogAppender(
            sender, this.config.getLevel(), delay, 
            new EMailLogFormatter(this.config.getLevel()));
        
        
        // create MailManager
        MailManagerImpl mailManager = new MailManagerImpl(this.config.getSender());
        this.provideComponent(mailManager);
        
        Logger.getRootLogger().addAppender(appender);
    }

    
}