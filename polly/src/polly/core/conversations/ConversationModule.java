package polly.core.conversations;

import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.SetupException;
import polly.core.ShutdownManagerImpl;
import polly.core.annotation.Module;
import polly.core.annotation.Provide;
import polly.core.annotation.Require;

@Module(
    requires = @Require(component = ShutdownManagerImpl.class),
    provides = @Provide(component = ConversationManagerImpl.class))
public class ConversationModule extends AbstractModule {

    public ConversationModule(ModuleLoader loader) {
        super("MODULE_CONVERSATIONS", loader, true);
    }
    
    

    @Override
    public void setup() throws SetupException {
        ConversationManagerImpl conversationManager = new ConversationManagerImpl();
        this.provideComponent(conversationManager);
        ShutdownManagerImpl shutdownManager = this.requireNow(ShutdownManagerImpl.class);
        shutdownManager.addDisposable(conversationManager);
    }

}
