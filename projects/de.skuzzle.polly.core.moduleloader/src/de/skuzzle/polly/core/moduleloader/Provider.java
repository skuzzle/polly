package de.skuzzle.polly.core.moduleloader;


public interface Provider {
       
    public abstract void addState(int state);
            
    public abstract void provideComponentAs(Class<?> type, Object component);
    
    public abstract void provideComponent(Object component);
    
    public abstract <T> T requireNow(Class<T> component, boolean check);

    public abstract boolean isCrucial();
    
    public abstract boolean isSetup();
    
    public abstract boolean isRun();
    
    public abstract String getName();
    
    public abstract void setupModule() throws SetupException;
    
    public abstract void runModule() throws Exception;    
    
    public abstract ModuleLoader getModuleLoder();
    
    public abstract void dispose();
}
