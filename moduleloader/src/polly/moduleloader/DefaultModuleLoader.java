package polly.moduleloader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import polly.moduleloader.annotations.None;
import polly.moduleloader.annotations.Provide;
import polly.moduleloader.annotations.Require;


public class DefaultModuleLoader implements ModuleLoader {

    private static Logger logger = Logger.getLogger(DefaultModuleLoader.class
        .getName());

    private Map<Class<?>, Module> setupProvides;
    private Map<Integer, Module> providedStates;;

    private Map<Module, Set<Class<?>>> beforeSetupReq;
    private Map<Module, Set<Integer>> requiredStates;
    private Map<Class<?>, Object> provides;
    private Set<Module> modules;
    private Set<Integer> state;



    public DefaultModuleLoader() {
        this.setupProvides = new HashMap<Class<?>, Module>();
        this.providedStates = new HashMap<Integer, Module>();
        this.beforeSetupReq = new HashMap<Module, Set<Class<?>>>();
        this.requiredStates = new HashMap<Module, Set<Integer>>();
        this.provides = new HashMap<Class<?>, Object>();
        this.modules = new HashSet<Module>();
        this.state = new HashSet<Integer>();
    }



    private void processModule(Module module) {
        Class<?> cls = module.getClass();

        polly.moduleloader.annotations.Module an = cls.getAnnotation(
        		polly.moduleloader.annotations.Module.class);

        if (an == null) {
            throw new ModuleDependencyException("module " + module
                + " is not annotated");
        }

        for (Provide p : an.provides()) {
            if (p.component() != None.class) {
                this.willProvideDuringSetup(p.component(), module);
            }
            if (p.state() >= 0) {
                this.willSetState(p.state(), module);
            }
        }

        for (Require r : an.requires()) {
            if (r.component() != None.class) {
                this.requireBeforeSetup(r.component(), module);
            }
            if (r.state() >= 0) {
                this.requireState(r.state(), module);
            }
        }
    }



    @Override
    public <T> void willProvideDuringSetup(Class<T> component, Module provider) {
        Module m = this.setupProvides.get(component);
        if (m != null) {
            throw new ModuleDependencyException("Module '" + provider
                + "' cannot provide component '" + component
                + "' because it is already provided by module '" + m + "'");
        }

        Set<Class<?>> requires = this.beforeSetupReq.get(provider);
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

        // TODO: check cyclic dependency

        Set<Class<?>> set = this.beforeSetupReq.get(module);
        if (set == null) {

            set = new HashSet<Class<?>>();
            this.beforeSetupReq.put(module, set);
        }
        set.add(component);
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
            throw new ModuleDependencyException("Provided component for '"
                + type + "' cannot be null");
        }
        this.provides.put(type, component);
    }



    @Override
    public void registerModule(Module module) {
        this.processModule(module);
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
            this.runModuleSetup(module, new HashSet<Module>());
        }
    }



    private void runModuleSetup(Module module, Set<Module> callSet)
        throws SetupException {
        if (module.isSetup()) {
            return;
        }

        callSet.add(module);
        Set<Class<?>> required = this.beforeSetupReq.get(module);
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
                } else if (callSet.contains(mod)) {
                    throw new ModuleDependencyException(
                        "invalid cyclic dependency between module '" + mod
                            + "' and '" + module + "'");
                } else if (mod != module) {
                    this.runModuleSetup(mod, callSet);
                }
            }
        }
        logger.info("Running setup for '" + module + "'");
        module.setupModule();
        callSet.remove(module);

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

        Set<Integer> set = this.requiredStates.get(module);
        if (set == null) {
            set = new HashSet<Integer>();
            this.requiredStates.put(module, set);
        }
        set.add(state);
    }



    @Override
    public void willSetState(int state, Module module) {
        Module m = this.providedStates.get(state);
        if (m != null) {
            throw new ModuleDependencyException("State '" + state
                + "' already provided by module '" + m + "'");
        }

        Set<Integer> requires = this.requiredStates.get(module);
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
            this.runModule(module, new HashSet<Module>());
        }
    }



    private void runModule(Module module, Set<Module> callSet) throws Exception {
        if (module.isRun()) {
            return;
        }

        callSet.add(module);
        Set<Integer> required = this.requiredStates.get(module);
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
                        "invalid dependency. no module provides state '"
                            + state + "' during run");
                } else if (callSet.contains(mod)) {
                    throw new ModuleDependencyException(
                        "invalid cyclic dependency between module '" + mod
                            + "' and '" + module + "'");
                } else if (mod != module) {
                    this.runModule(mod, callSet);
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
