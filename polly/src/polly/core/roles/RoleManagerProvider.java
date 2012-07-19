package polly.core.roles;


import de.skuzzle.polly.sdk.roles.RoleManager;
import polly.core.ModuleStates;
import polly.core.persistence.PersistenceManagerImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;
import polly.moduleloader.annotations.Require;

@Module(
    requires = {
        @Require(component = PersistenceManagerImpl.class),
        @Require(state = ModuleStates.PERSISTENCE_READY)
    },
    provides = {
        @Provide(component = RoleManagerImpl.class),
        @Provide(state = ModuleStates.ROLES_READY)
    }
)
public class RoleManagerProvider extends AbstractModule {
    
    private RoleManagerImpl roleManager;
    
    public RoleManagerProvider(ModuleLoader loader) {
        super("ROLE_MANAGER_PROVIDER", loader, true);
    }
    
    

    @Override
    public void setup() throws SetupException {
        PersistenceManagerImpl persistence = 
                this.requireNow(PersistenceManagerImpl.class);
        
        this.roleManager = new RoleManagerImpl(persistence);
        
        this.provideComponent(roleManager);
    }
    
    
    
    @Override
    public void run() throws Exception {
        // Add default roles and permissions
        this.roleManager.createRole(RoleManager.ADMIN_ROLE);
        this.roleManager.createRole(RoleManager.DEFAULT_ROLE);
        
        this.roleManager.registerPermission(RoleManager.NONE_PERMISSION);
        this.roleManager.registerPermission(RoleManager.REGISTERED_PERMISSION);
        this.roleManager.registerPermission(RoleManager.ADMIN_PERMISSION);
        
        this.roleManager.assignPermission(RoleManager.DEFAULT_ROLE, 
            RoleManager.REGISTERED_PERMISSION);
        
        this.roleManager.assignPermission(RoleManager.ADMIN_ROLE, 
            RoleManager.ADMIN_PERMISSION);
        
        this.addState(ModuleStates.ROLES_READY);
    }
}
