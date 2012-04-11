package core;

import java.util.HashMap;
import java.util.Map;


public class OnActionSet {

    private Map<String, Integer> actions;
    
    
    public OnActionSet() {
        this.actions = new HashMap<String, Integer>();
    }
    
    
    
    public void put(String forUser) {
        this.add(forUser, 1);
    }
    
    
    
    public void take(String forUser) {
        this.add(forUser, 1);
    }
    
    
    
    public boolean available(String forUser) {
        synchronized (this.actions) {
            Integer i = this.actions.get(forUser);
            return i != null && i > 0;
        }
    }
    
    
    
    public void clear() {
        this.actions.clear();
    }
    
    
    
    private void add(String forUser, int i) {
        synchronized (this.actions) {
            Integer j = this.actions.get(forUser);
            if (j == null) {
                j = 0;
            }
            j += i;
            if (j == 0) {
                this.actions.remove(forUser);
                return;
            }
            this.actions.put(forUser, j);
        }
    }
}