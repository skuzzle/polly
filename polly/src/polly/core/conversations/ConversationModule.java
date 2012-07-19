package polly.core.conversations;

import polly.core.ShutdownManagerImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = @Require(component = ShutdownManagerImpl.class),
    provides = @Provide(component = ConversationManagerImpl.class))
public class ConversationModule extends AbstractModule {

    public ConversationModule(ModuleLoader loader) {
        super("CONVERSATION_MANAGER_PROVIDER", loader, true);
    }
    
    

    @Override
    public void setup() throws SetupException {
        ConversationManagerImpl conversationManager = new ConversationManagerImpl();
        this.provideComponent(conversationManager);
        ShutdownManagerImpl shutdownManager = this.requireNow(ShutdownManagerImpl.class);
        shutdownManager.addDisposable(conversationManager);
    }

}
