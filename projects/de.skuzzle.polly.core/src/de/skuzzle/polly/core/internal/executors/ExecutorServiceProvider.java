package de.skuzzle.polly.core.internal.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;
import de.skuzzle.polly.tools.events.AsynchronousEventProvider;
import de.skuzzle.polly.tools.events.EventProvider;

import polly.moduleloader.AbstractProvider;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;

@Module(
    requires = { 
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class) }, 
    provides = {
        @Provide(component = EventProvider.class),
        @Provide(component = ExecutorService.class) })
public class ExecutorServiceProvider extends AbstractProvider {

    
    public ExecutorServiceProvider(ModuleLoader loader) {
        super("EXECUTOR_SERVICE_PROVIDER", loader, true);
    }



    @Override
    public void setup() {
        ConfigurationProviderImpl configProvider = 
            this.requireNow(ConfigurationProviderImpl.class, true);
        Configuration pollyCfg = configProvider.getRootConfiguration();

        ExecutorService eventThreadPool = Executors.newFixedThreadPool(
            pollyCfg.readInt(Configuration.EVENT_THREADS),
            new ThreadFactoryBuilder("EVENT_THREAD_%n%"));
        final EventProvider eventProvider = new AsynchronousEventProvider(eventThreadPool);

        final ExecutorService commandExecutor = Executors.newFixedThreadPool(
            pollyCfg.readInt(Configuration.EXECUTION_THREADS), 
            new ThreadFactoryBuilder(
            "EXECUTION_THREAD_%n%"));

        this.provideComponentAs(EventProvider.class, eventProvider);
        this.provideComponentAs(ExecutorService.class, commandExecutor);

        ShutdownManagerImpl shutdownManager = this
            .requireNow(ShutdownManagerImpl.class, true);
        shutdownManager.addDisposable(new AbstractDisposable() {
            @Override
            protected void actualDispose() throws DisposingException {
                eventProvider.dispose();
                commandExecutor.shutdown();
            }
        });
    }

}
