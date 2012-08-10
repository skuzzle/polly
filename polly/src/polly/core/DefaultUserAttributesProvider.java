package polly.core;

import de.skuzzle.polly.sdk.constraints.Constraints;
import polly.core.users.UserManagerImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;

@Module(
    requires = {
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.USERS_READY)
    }
)
public class DefaultUserAttributesProvider extends AbstractModule {
    
    public final static String AUTO_LOGON = "AUTO_LOGON"; 

    public DefaultUserAttributesProvider(ModuleLoader loader) {
        super("DEFAULT_ATTRIBUTES_PROVIDER", loader, false);
    }

    
    
    @Override
    public void run() throws Exception {
        UserManagerImpl userManager = this.requireNow(UserManagerImpl.class, false);
        
        try {
            userManager.addAttribute(AUTO_LOGON, "true", Constraints.BOOLEAN);
        } catch (Exception e) {
            throw new SetupException(e);
        }
    }



    @Override
    public void setup() throws SetupException {}
}
