package polly.core.conversations;

import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.SetupException;
import polly.core.ShutdownManagerImpl;


public class ConversationModule extends AbstractModule {

    public ConversationModule(ModuleLoader loader) {
        super("MODULE_CONVERSATIONS", loader, true);
        this.requireBeforeSetup(ShutdownManagerImpl.class);
        this.willProvideDuringSetup(ConversationManagerImpl.class);
    }
    
    

    @Override
    public void setup() throws SetupException {
        ConversationManagerImpl conversationManager = new ConversationManagerImpl();
        this.provideComponent(conversationManager);
        ShutdownManagerImpl shutdownManager = this.requireNow(ShutdownManagerImpl.class);
        shutdownManager.addDisposable(conversationManager);
    }

}
