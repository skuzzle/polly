package de.skuzzle.polly.core.internal.status;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.StatusManager;


public class StatusManagerImpl implements StatusManager {

    private final MyPolly myPolly;
    private final Map<String, StatusProvider> providers;
    
    
    
    public StatusManagerImpl(MyPolly myPolly) {
        this.myPolly = myPolly;
        this.providers = new HashMap<>();
    }


    
    @Override
    public void registerStatusProvider(String statusKey, StatusProvider provider) {
        if (this.providers.containsKey(statusKey)) {
            throw new IllegalArgumentException(String.format(
                    "StatusProvider '%s' already exists", statusKey)); //$NON-NLS-1$
        }
        this.providers.put(statusKey, provider);
    }

    

    @Override
    public void removeStatusProvider(String statusKey) {
        this.providers.remove(statusKey);
    }

    

    @Override
    public String getCurrentStatus(String statusKey) {
        final StatusProvider sp = this.providers.get(statusKey);
        if (sp != null) {
            return sp.provide(this.myPolly);
        }
        return ""; //$NON-NLS-1$
    }
    
    
    
    @Override
    public Map<String, String> getCurrentStatusMap() {
        return this.providers.entrySet().stream()
                .collect(Collectors.toMap(
                        e-> e.getKey(), e -> e.getValue().provide(myPolly)));
    }
}
