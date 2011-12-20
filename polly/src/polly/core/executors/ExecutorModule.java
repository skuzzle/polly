package polly.core.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import polly.configuration.PollyConfiguration;
import polly.events.DefaultEventProvider;
import polly.events.EventProvider;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;
import polly.util.concurrent.ThreadFactoryBuilder;


public class ExecutorModule extends AbstractPollyModule {

    private PollyConfiguration config;
    
    public ExecutorModule(ModuleBlackboard initializer) {
        super("EXECUTORS", initializer, true);
    }

    
    
    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
    }
    
    
    
    @Override
    public boolean doSetup() throws Exception {
        ExecutorService eventThreadPool = Executors.newFixedThreadPool
            (this.config.getEventThreads(), 
            new ThreadFactoryBuilder("EVENT_THREAD_%n%"));
        EventProvider eventProvider = new DefaultEventProvider(eventThreadPool);
    
        
        ExecutorService commandExecutor = Executors.newFixedThreadPool(
                config.getExecutionThreads(), 
                new ThreadFactoryBuilder("EXECUTION_THREAD_%n"));
        
        this.provideComponent(EventProvider.class, eventProvider);
        this.provideComponent(ExecutorService.class, commandExecutor);
        
        return true;
    }
    

    @Override
    public void doRun() throws Exception {}



}
