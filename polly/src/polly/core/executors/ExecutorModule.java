package polly.core.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.exceptions.DisposingException;

import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.events.DefaultEventProvider;
import polly.events.EventProvider;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;
import polly.util.concurrent.ThreadFactoryBuilder;

@Module(
    requires = { 
        @Require(component = PollyConfiguration.class),
        @Require(component = ShutdownManagerImpl.class) }, 
    provides = {
        @Provide(component = EventProvider.class),
        @Provide(component = ExecutorService.class) })
public class ExecutorModule extends AbstractModule {

    public ExecutorModule(ModuleLoader loader) {
        super("EXECUTOR_SERVICE_PROVIDER", loader, true);
    }



    @Override
    public void setup() {
        PollyConfiguration config = this.requireNow(PollyConfiguration.class);

        ExecutorService eventThreadPool = Executors.newFixedThreadPool(config
            .getEventThreads(), new ThreadFactoryBuilder("EVENT_THREAD_%n%"));
        EventProvider eventProvider = new DefaultEventProvider(eventThreadPool);

        final ExecutorService commandExecutor = Executors.newFixedThreadPool(
            config.getExecutionThreads(), new ThreadFactoryBuilder(
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
