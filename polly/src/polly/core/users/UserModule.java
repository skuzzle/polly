package polly.core.users;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.core.persistence.PersistenceManagerImpl;
import polly.data.User;
import polly.events.EventProvider;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;


public class UserModule extends AbstractPollyModule {

    private PersistenceManagerImpl persistenceManager;
    private EventProvider eventProvider;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;
    private UserManagerImpl userManager;
    
    public UserModule(ModuleBlackboard initializer) {
        super("USER", initializer, true);
    }
    
    
    
    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
        this.eventProvider = this.requireComponent(EventProvider.class);
        this.persistenceManager = this.requireComponent(PersistenceManagerImpl.class);
        this.shutdownManager = this.requireComponent(ShutdownManagerImpl.class);
    }

    
    
    @Override
    public boolean doSetup() throws Exception {
        this.userManager = new UserManagerImpl(
            this.persistenceManager, 
            this.config.getDeclarationCachePath(), 
            this.eventProvider);
        this.provideComponent(UserManagerImpl.class, this.userManager);
        this.shutdownManager.addDisposable(this.userManager);

        return true;
    }

    
    
    @Override
    public void doRun() throws Exception {
        try {
            logger.info("Creating default user with name '" + 
                    this.config.getAdminUserName() + "'.");
            User admin = new User(
                    this.config.getAdminUserName(), 
                    "", 
                    this.config.getAdminUserLevel());
            
            admin.setHashedPassword(this.config.getAdminPasswordHash());
            this.userManager.addUser(admin);
        }  catch (UserExistsException e) {
            logger.debug("Default user already existed.");
        } catch (DatabaseException e) {
            logger.fatal("Database error", e);
        }
    }



}
