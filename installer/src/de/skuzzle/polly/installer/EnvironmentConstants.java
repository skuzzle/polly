package de.skuzzle.polly.installer;

import java.util.HashMap;
import java.util.Map;


public class EnvironmentConstants extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;

    public EnvironmentConstants() {
        super();
    }

    public EnvironmentConstants(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public EnvironmentConstants(int initialCapacity) {
        super(initialCapacity);
    }

    public EnvironmentConstants(Map<? extends String, ? extends String> m) {
        super(m);
    }
    
    
    public String resolve(String s) {
        for (Map.Entry<String, String> e : this.entrySet()) {
            s = s.replace(e.getKey(), e.getValue());
        }
        return s;
    }

}
