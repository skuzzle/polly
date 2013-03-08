package de.skuzzle.polly.core.moduleloader;

import java.io.File;
import java.io.IOException;



public interface ModuleLoader {
    
    public void exportToDot(File output) throws IOException;

    public abstract void willSetState(int state, Provider provider);
    
    public abstract void requireState(int state, Provider provider);
    
    public abstract boolean isStateSet(int state);
    
    public abstract void addState(int state);
    
    public abstract <T> void willProvideDuringSetup(Class<T> component, Provider provider);
    
    public abstract <T> void requireBeforeSetup(Class<?> component, Provider provider);
    
    public abstract void provideComponentAs(Class<?> type, Object component); 
    
    public abstract void provideComponent(Object component);
    
    public abstract <T> T requireNow(Class<T> component);
    
    public abstract void registerModule(Provider provider);
    
    public abstract void runSetup() throws SetupException;

    public abstract void runModules() throws Exception;
    
    public abstract void dispose();

    boolean checkRequires(Class<?> component, Provider provider);
    
}
