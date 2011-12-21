package polly.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

public class DefaultModuleLoader implements ModuleLoader {

    private static Logger logger = Logger.getLogger(DefaultModuleLoader.class
        .getName());

    private Map<Class<?>, Module> setupProvides;
    private Map<Integer, Module> providedStates;;

    private Map<Module, List<Class<?>>> beforeSetupReq;
    private Map<Module, List<Integer>> requiredStates;
    private Map<Class<?>, Object> provides;
    private Set<Module> modules;
    private Set<Integer> state;



    public DefaultModuleLoader() {
        this.setupProvides = new HashMap<Class<?>, Module>();
        this.providedStates = new HashMap<Integer, Module>();
        this.beforeSetupReq = new HashMap<Module, List<Class<?>>>();
        this.requiredStates = new HashMap<Module, List<Integer>>();
        this.provides = new HashMap<Class<?>, Object>();
        this.modules = new HashSet<Module>();
        this.state = new HashSet<Integer>();
    }



    @Override
    public <T> void willProvideDuringSetup(Class<T> component, Module provider) {
        if (this.setupProvides.containsKey(component)) {
            throw new ModuleDependencyException("Component '" + component
                + "' already provided");
        }

        List<Class<?>> requires = this.beforeSetupReq.get(provider);
        if (requires != null && requires.contains(component)) {
            throw new ModuleDependencyException("Module '" + provider + ""
                + "' cannot provide '" + component
                + "' because it already requires it.");
        }
        this.setupProvides.put(component, provider);
    }



    @Override
    public <T> void requireBeforeSetup(Class<?> component, Module module) {
        Module mod = this.setupProvides.get(component);
        if (mod == module) {
            throw new ModuleDependencyException("Module '" + module
                + "' cannot require '" + component
                + "' because it already provides it.");
        }

        List<Class<?>> list = this.beforeSetupReq.get(module);
        if (list == null) {
            list = new ArrayList<Class<?>>();
            this.beforeSetupReq.put(module, list);
        }
        list.add(component);
    }



    @Override
    public void provideComponent(Object component) {
        this.provideComponentAs(component.getClass(), component);
    }



    @Override
    public void provideComponentAs(Class<?> type, Object component) {
        if (type == null) {
            throw new ModuleDependencyException("Provided type cannot be null");
        } else if (component == null) {
            throw new ModuleDependencyException("Provided component for '" + type + 
                "' cannot be null");
        }
        this.provides.put(type, component);
    }



    @Override
    public void registerModule(Module module) {
        this.modules.add(module);
    }



    @Override
    public <T> T requireNow(Class<T> component) {
        Object comp = this.provides.get(component);
        if (comp != null) {
            return component.cast(comp);
        } else {
            throw new IllegalArgumentException("component '" + component
                + "' not provided");
        }
    }



    private boolean isProvided(Class<?> component) {
        return this.provides.get(component) != null;
    }



    @Override
    public void runSetup() throws SetupException {
        for (Module module : this.modules) {
            this.runModuleSetup(module);
        }
    }



    private void runModuleSetup(Module module) throws SetupException {
        if (module.isSetup()) {
            return;
        }

        List<Class<?>> required = this.beforeSetupReq.get(module);
        if (required != null) {

            logger.trace("Resolving " + required.size()
                + " requirements for module '" + module + "': ");

            for (Class<?> component : required) {

                if (this.isProvided(component)) {
                    continue;
                }

                // find module that provides this component
                Module mod = this.setupProvides.get(component);
                if (mod == null) {
                    throw new ModuleDependencyException(
                        "invalid dependency. no module provides '" + component
                            + "' during setup");
                } else if (mod != module) {
                    this.runModuleSetup(mod);
                }
            }
        }
        logger.info("Running setup for '" + module + "'");
        module.setupModule();

        // check if all components that 'module' claimed to provide are
        // actually provided now
        for (Entry<Class<?>, Module> entry : this.setupProvides.entrySet()) {
            if (entry.getValue() == module && !this.isProvided(entry.getKey())) {
                throw new ModuleDependencyException("Module '" + module
                    + "' claimed to provide '" + entry.getKey()
                    + "' but did not");
            }
        }
    }



    @Override
    public boolean isStateSet(int state) {
        return this.state.contains(state);
    }

    
    
    @Override
    public void addState(int state) {
        this.state.add(state);
    }


    @Override
    public void requireState(int state, Module module) {
        Module mod = this.providedStates.get(state);
        if (mod == module) {
            throw new ModuleDependencyException("Module '" + module
                + "' cannot require state '" + state
                + "' because it already provides it.");
        }

        List<Integer> list = this.requiredStates.get(module);
        if (list == null) {
            list = new ArrayList<Integer>();
            this.requiredStates.put(module, list);
        }
        list.add(state);
    }



    @Override
    public void willSetState(int state, Module module) {
        Module m = this.providedStates.get(state);
        if (m != null) {
            throw new ModuleDependencyException("State '" + state
                + "' already provided by module '" + m + "'");
        }

        List<Integer> requires = this.requiredStates.get(module);
        if (requires != null && requires.contains(state)) {
            throw new ModuleDependencyException("Module '" + module + ""
                + "' cannot provide state '" + state
                + "' because it already requires it.");
        }
        this.providedStates.put(state, module);
    }



    @Override
    public void runModules() throws Exception {
        for (Module module : this.modules) {
            this.runModule(module);
        }
    }



    private void runModule(Module module) throws Exception {
        if (module.isRun()) {
            return;
        }

        List<Integer> required = this.requiredStates.get(module);
        if (required != null) {

            logger.trace("Requiring " + required.size()
                + " states for module '" + module + "': ");

            for (Integer state : required) {

                if (this.isStateSet(state)) {
                    continue;
                }

                // find module that provides this component
                Module mod = this.providedStates.get(state);
                if (mod == null) {
                    throw new ModuleDependencyException(
                        "invalid dependency. no module provides state '" + state
                            + "' during run");
                } else if (mod != module) {
                    this.runModule(mod);
                }
            }
        }
        logger.info("Running setup for '" + module + "'");
        module.runModule();

        // check if all states that 'module' claimed to provide are
        // actually provided now
        for (Entry<Integer, Module> entry : this.providedStates.entrySet()) {
            if (entry.getValue() == module && !this.isStateSet(entry.getKey())) {
                throw new ModuleDependencyException("Module '" + module
                    + "' claimed to provide state '" + entry.getKey()
                    + "' but did not");
            }
        }
    }

}
