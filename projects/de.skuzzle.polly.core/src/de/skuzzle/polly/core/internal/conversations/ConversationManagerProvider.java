package de.skuzzle.polly.core.internal.conversations;

import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;

@Module(
    requires = @Require(component = ShutdownManagerImpl.class),
    provides = @Provide(component = ConversationManagerImpl.class))
public class ConversationManagerProvider extends AbstractProvider {

    public ConversationManagerProvider(ModuleLoader loader) {
        super("CONVERSATION_MANAGER_PROVIDER", loader, true); //$NON-NLS-1$
    }
    
    

    @Override
    public void setup() throws SetupException {
        ConversationManagerImpl conversationManager = new ConversationManagerImpl();
        this.provideComponent(conversationManager);
        ShutdownManagerImpl shutdownManager = this.requireNow(
                ShutdownManagerImpl.class, true);
        shutdownManager.addDisposable(conversationManager);
    }

}
