package polly.core.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.exceptions.DisposingException;

import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.ShutdownManagerImpl;
import polly.core.annotation.Module;
import polly.core.annotation.Provide;
import polly.core.annotation.Require;
import polly.events.DefaultEventProvider;
import polly.events.EventProvider;
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
        super("MODULE_EXECUTORS", loader, true);
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
