package polly.core.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.exceptions.DisposingException;

import polly.configuration.ConfigurationProviderImpl;
import polly.core.ShutdownManagerImpl;
import polly.events.AsynchronousEventProvider;
import polly.events.EventProvider;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;
import polly.util.concurrent.ThreadFactoryBuilder;

@Module(
    requires = { 
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class) }, 
    provides = {
        @Provide(component = EventProvider.class),
        @Provide(component = ExecutorService.class) })
public class ExecutorServiceProvider extends AbstractModule {

    
    public ExecutorServiceProvider(ModuleLoader loader) {
        super("EXECUTOR_SERVICE_PROVIDER", loader, true);
    }



    @Override
    public void setup() {
        ConfigurationProviderImpl configProvider = 
            this.requireNow(ConfigurationProviderImpl.class);
        Configuration pollyCfg = configProvider.getRootConfiguration();

        ExecutorService eventThreadPool = Executors.newFixedThreadPool(
            pollyCfg.readInt(Configuration.EVENT_THREADS),
            new ThreadFactoryBuilder("EVENT_THREAD_%n%"));
        EventProvider eventProvider = new AsynchronousEventProvider(eventThreadPool);

        final ExecutorService commandExecutor = Executors.newFixedThreadPool(
            pollyCfg.readInt(Configuration.EXECUTION_THREADS), 
            new ThreadFactoryBuilder(
            "EXECUTION_THREAD_%n%"));

        this.provideComponentAs(EventProvider.class, eventProvider);
        this.provideComponentAs(ExecutorService.class, commandExecutor);

        ShutdownManagerImpl shutdownManager = this
            .requireNow(ShutdownManagerImpl.class);
        shutdownManager.addDisposable(eventProvider);
        shutdownManager.addDisposable(new AbstractDisposable() {

            @Override
            protected void actualDispose() throws DisposingException {
                commandExecutor.shutdown();
            }
        });
    }

}
