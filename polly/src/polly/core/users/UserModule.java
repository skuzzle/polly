package polly.core.users;


import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import polly.configuration.PollyConfiguration;
import polly.core.ModuleStates;
import polly.core.ShutdownManagerImpl;
import polly.core.persistence.PersistenceManagerImpl;
import polly.core.roles.RoleManagerImpl;
import polly.events.EventProvider;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;



@Module(
    requires = {
        @Require(component = PollyConfiguration.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = EventProvider.class),
        @Require(component = PersistenceManagerImpl.class),
        @Require(component = RoleManagerImpl.class),
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.ROLES_READY)
    },
    provides = {
        @Provide(component = UserManagerImpl.class),
        @Provide(state = ModuleStates.USERS_READY)
    })
public class UserModule extends AbstractModule {

    private PersistenceManagerImpl persistenceManager;
    private EventProvider eventProvider;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;
    private UserManagerImpl userManager;
    private RoleManagerImpl roleManager;


    public UserModule(ModuleLoader loader) {
        super("USER_MANAGER_PROVIDER", loader, true);
    }



    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.eventProvider = this.requireNow(EventProvider.class);
        this.persistenceManager = this.requireNow(PersistenceManagerImpl.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
        this.roleManager = this.requireNow(RoleManagerImpl.class);
    }



    @Override
    public void setup() throws SetupException {
        this.userManager = new UserManagerImpl(this.persistenceManager,
            this.config, this.eventProvider, this.roleManager);
        this.provideComponent(this.userManager);
        this.shutdownManager.addDisposable(this.userManager);
    }
    


    @Override
    public void run() throws Exception {
        de.skuzzle.polly.sdk.model.User admin = null;
        try {
            logger.info("Creating default user with name '"
                + this.config.getAdminUserName() + "'.");
            admin = this.userManager.createUser(this.config.getAdminUserName(), "",
                this.config.getAdminUserLevel());

            admin.setHashedPassword(this.config.getAdminPasswordHash());
            this.userManager.addUser(admin);
        } catch (UserExistsException e) {
            admin = e.getUser();
            logger.debug("Default user already existed.");
        } catch (DatabaseException e) {
            logger.fatal("Database error", e);
        } finally {
            this.userManager.setAdmin(admin);
            this.addState(ModuleStates.USERS_READY);
            this.roleManager.assignRole(admin, RoleManager.ADMIN_ROLE);
        }
    }

    
    
    @Override
    public void dispose() {
        this.config = null;
        this.eventProvider = null;
        this.persistenceManager = null;
        this.shutdownManager = null;
        this.userManager = null;
        super.dispose();
    }
}
