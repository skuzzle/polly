package polly.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ModuleBlackboard {
    
    private Map<Class<?>, Object> components;
    private List<AbstractPollyModule> all;
    private List<Action> afterSetupActions;
    private List<Action> afterRunActions;
    
    
    public static abstract class Action {
        
        protected ModuleBlackboard blackBoard;
        
        public Action(ModuleBlackboard blackboard) {
            this.blackBoard = blackboard;
        }
        
        
        public abstract void action();

    }
    
    
    public ModuleBlackboard() {
        this.components = new HashMap<Class<?>, Object>();
        this.all = new ArrayList<AbstractPollyModule>();
        this.afterSetupActions = new ArrayList<ModuleBlackboard.Action>();
        this.afterRunActions = new ArrayList<ModuleBlackboard.Action>();
    }
    
    
    
    public void addAfterSetupAction(Action action) {
        this.afterSetupActions.add(action);
    }
    
    public void addAfterRunAction(Action action) {
        this.afterRunActions.add(action);
    }
    
    
    public void registerModule(AbstractPollyModule component) {
        if (this.all.contains(component)) {
            throw new IllegalArgumentException("component already registered");
        } else {
            this.all.add(component);
        }
    }

    
    
    /**
     * Sets up all components in the order they are registered. If a crucial component
     * fails during setup, this method immediately returns. If setup was successful, all
     * 'AfterSetupActions' are executed.
     * 
     * @return Whether the setup was successful. That is, all crucial components could
     *          be setup successful.
     */
    public boolean setupAll() {
        for (AbstractPollyModule component : this.all) {
            boolean setup = component.setup();
            
            if (!setup && component.isCrucial()) {
                return false;
            }
        }
        
        for (Action action : this.afterSetupActions) {
            action.action();
        }
        return true;
    }
    
    
    
    public void runAll() {
        for (AbstractPollyModule component : this.all) {
            if (!component.isSetup()) {
                throw new IllegalArgumentException("component not setup");
            }
            
            component.run();
        }
        
        for (Action action : this.afterRunActions) {
            action.action();
        }
    }
    
    
    
    public void run(String module) {
        AbstractPollyModule key = new AbstractPollyModule(module, null) {
            @Override
            public void require() {}
            @Override
            public boolean doSetup() throws Exception {
                return true;
            }
            @Override
            public void doRun() throws Exception { }
        };
        
        int i = this.all.indexOf(key);
        if (i != -1) {
            AbstractPollyModule mod = this.all.get(i);
            if (mod.isSetup() && !mod.isRun()) {
                mod.run();
            }
        }
    }
    
    
    
    public <T> void provideComponent(Class<T> cls, T component) {
        this.components.put(cls, component);
    }
    
    
    
    public <T> boolean isProvided(Class<T> clazz) {
        return this.components.get(clazz) != null;
    }
    
    
    
    public <T> T requireComponent(Class<T> clazz) {
        Object comp = this.components.get(clazz);
        
        if (clazz != null && clazz.isInstance(comp)) {
            return clazz.cast(comp);
        } else {
            throw new IllegalArgumentException("No provider for " + clazz.getName());
        }
    }
}