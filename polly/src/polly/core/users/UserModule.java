package polly.core.users;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.ModuleStates;
import polly.core.ShutdownManagerImpl;
import polly.core.persistence.PersistenceManagerImpl;
import polly.data.User;
import polly.events.EventProvider;

public class UserModule extends AbstractModule {

    private PersistenceManagerImpl persistenceManager;
    private EventProvider eventProvider;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;
    private UserManagerImpl userManager;



    public UserModule(ModuleLoader loader) {
        super("MODULE_USER", loader, true);

        this.requireBeforeSetup(PollyConfiguration.class);
        this.requireBeforeSetup(EventProvider.class);
        this.requireBeforeSetup(PersistenceManagerImpl.class);
        this.requireBeforeSetup(ShutdownManagerImpl.class);

        this.willProvideDuringSetup(UserManagerImpl.class);
        
        this.willSetState(ModuleStates.USERS_READY);
        
        this.requireState(ModuleStates.PERSISTENCE_READY);
    }



    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.eventProvider = this.requireNow(EventProvider.class);
        this.persistenceManager = this.requireNow(PersistenceManagerImpl.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
    }



    @Override
    public void setup() throws SecurityException {
        this.userManager = new UserManagerImpl(this.persistenceManager,
            this.config.getDeclarationCachePath(), this.eventProvider);
        this.provideComponent(this.userManager);
        this.shutdownManager.addDisposable(this.userManager);
    }


    @Override
    public void run() throws Exception {
        try {
            logger.info("Creating default user with name '"
                + this.config.getAdminUserName() + "'.");
            User admin = new User(this.config.getAdminUserName(), "",
                this.config.getAdminUserLevel());

            admin.setHashedPassword(this.config.getAdminPasswordHash());
            this.userManager.addUser(admin);
        } catch (UserExistsException e) {
            logger.debug("Default user already existed.");
        } catch (DatabaseException e) {
            logger.fatal("Database error", e);
        } finally {
            this.addState(ModuleStates.USERS_READY);
        }
    }

}
