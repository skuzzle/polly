package polly.core.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.events.DefaultEventProvider;
import polly.events.EventProvider;
import polly.util.concurrent.ThreadFactoryBuilder;


public class ExecutorModule extends AbstractModule {

    public ExecutorModule(ModuleLoader loader) {
        super("MODULE_EXECUTORS", loader, true);
        this.requireBeforeSetup(PollyConfiguration.class);
        this.willProvideDuringSetup(EventProvider.class);
        this.willProvideDuringSetup(ExecutorService.class);
    }

    
    
    @Override
    public void setup() {
        PollyConfiguration config = this.requireNow(PollyConfiguration.class);
        
        ExecutorService eventThreadPool = Executors.newFixedThreadPool(
            config.getEventThreads(), 
            new ThreadFactoryBuilder("EVENT_THREAD_%n%"));
        EventProvider eventProvider = new DefaultEventProvider(eventThreadPool);
    
        
        ExecutorService commandExecutor = Executors.newFixedThreadPool(
                config.getExecutionThreads(), 
                new ThreadFactoryBuilder("EXECUTION_THREAD_%n%"));
        
        this.provideComponentAs(EventProvider.class, eventProvider);
        this.provideComponentAs(ExecutorService.class, commandExecutor);
    }


}
