package polly.util;

import java.util.HashMap;
import java.util.Map;

public class CaseInsensitiveStringKeyMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = 1L;

    public CaseInsensitiveStringKeyMap() {
        super();
    }

    public CaseInsensitiveStringKeyMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveStringKeyMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveStringKeyMap(Map<? extends String, ? extends V> m) {
        super(m);
    }
    
    
    @Override
    public boolean containsKey(Object key) {
        String k = key == null ? null : ((String) key).toLowerCase();
        return super.containsKey(k);
    }
    
    
    @Override
    public V get(Object key) {
        String k = key == null ? null : ((String) key).toLowerCase();
        return super.get(k);
    }
    
    
    public V put(String key, V value) {
        String k = key == null ? null : key.toLowerCase();
        return super.put(k, value);
    }
    
    
    @Override
    public V remove(Object key) {
        String k = key == null ? null : ((String) key).toLowerCase();
        return super.remove(k);
    }
}