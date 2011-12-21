package polly.core;


public interface Module {
    
    public abstract void willSetState(int state);
    
    public abstract void requireState(int state);
    
    public abstract boolean isStateSet(int state);
    
    public abstract void addState(int state);

    public abstract <T> void willProvideDuringSetup(Class<T> component);
       
    public abstract <T> void requireBeforeSetup(Class<T> component);
       
    public abstract void provideComponentAs(Class<?> type, Object component);
    
    public abstract void provideComponent(Object component);
    
    public abstract <T> T requireNow(Class<T> component);
    
    public abstract ModuleLoader getModuleLoader();

    public abstract boolean isCrucial();
    
    public abstract boolean isSetup();
    
    public abstract boolean isRun();
    
    public abstract String getName();
    
    public abstract void setupModule() throws SetupException;
    
    public abstract void runModule() throws Exception;
}
