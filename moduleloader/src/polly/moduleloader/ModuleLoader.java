package polly.moduleloader;



public interface ModuleLoader {

    public abstract void willSetState(int state, Module module);
    
    public abstract void requireState(int state, Module module);
    
    public abstract boolean isStateSet(int state);
    
    public abstract void addState(int state);
    
    public abstract <T> void willProvideDuringSetup(Class<T> component, Module provider);
    
    public abstract <T> void requireBeforeSetup(Class<?> component, Module module);
    
    public abstract void provideComponentAs(Class<?> type, Object component); 
    
    public abstract void provideComponent(Object component);
    
    public abstract <T> T requireNow(Class<T> component);
    
    public abstract void registerModule(Module module);
    
    public abstract void runSetup() throws SetupException;

    public abstract void runModules() throws Exception;
    
}
