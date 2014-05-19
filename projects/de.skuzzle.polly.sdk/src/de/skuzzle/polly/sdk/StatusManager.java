package de.skuzzle.polly.sdk;

import java.util.Map;

public interface StatusManager {

    @FunctionalInterface
    public static interface StatusProvider {
        public String provide(MyPolly myPolly);
    }
    
    
    
    public void registerStatusProvider(String statusKey, StatusProvider provider);
    
    public void removeStatusProvider(String statusKey);
    
    public String getCurrentStatus(String statusKey);
    
    public Map<String, String> getCurrentStatusMap();
}
