package polly.core.mail;



import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;


@Module(provides = {
    @Provide(component = MailManagerImpl.class),
    @Provide(component = MailConfig.class)})
public class MailManagerProvider extends AbstractModule {

    
    
    public MailManagerProvider(ModuleLoader loader) {
        super("MAIL_MANAGER_PROVIDER", loader, false);
    }


    private MailConfig config;
    
    
    
    @Override
    public void setup() throws SetupException {
        try {
            this.config = new MailConfig("cfg/mail.cfg");
        } catch (Exception e) {
            throw new SetupException(e);
        }
        
        this.provideComponent(this.config);
        
        MailManagerImpl mailManager = new MailManagerImpl(this.config.getSender());
        this.provideComponent(mailManager);
    }

    
}