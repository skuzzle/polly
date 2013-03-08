package de.skuzzle.polly.core.internal.roles;


import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.internal.persistence.PersistenceManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.roles.RoleManager;

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
public class RoleManagerProvider extends AbstractProvider {
    
    private RoleManagerImpl roleManager;
    
    public RoleManagerProvider(ModuleLoader loader) {
        super("ROLE_MANAGER_PROVIDER", loader, true);
    }
    
    

    @Override
    public void setup() throws SetupException {
        PersistenceManagerImpl persistence = 
                this.requireNow(PersistenceManagerImpl.class, true);
        
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
