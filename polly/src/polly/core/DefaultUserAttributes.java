package polly.core;

import de.skuzzle.polly.sdk.constraints.BooleanConstraint;
import polly.core.users.UserManagerImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;
import polly.moduleloader.annotations.Require;

@Module(
    requires = {
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.USERS_READY)
    },
    provides = @Provide(component = DefaultUserAttributes.class))
public class DefaultUserAttributes extends AbstractModule {
    
    public final static String AUTO_LOGON = "AUTO_LOGON"; 

    public DefaultUserAttributes(ModuleLoader loader) {
        super("DEFAULT_ATTRIBUTES", loader, false);
    }

    
    
    @Override
    public void setup() throws SetupException {
        this.provideComponent(this);
    }

    
    
    @Override
    public void run() throws Exception {
        UserManagerImpl userManager = this.requireNow(UserManagerImpl.class);
        
        try {
            userManager.addAttribute(AUTO_LOGON, "true", new BooleanConstraint());
        } catch (Exception e) {
            throw new SetupException(e);
        }
    }
}
