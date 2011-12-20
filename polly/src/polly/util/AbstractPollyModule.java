package polly.util;

import org.apache.log4j.Logger;


public abstract class AbstractPollyModule {
    
    private String name;
    private ModuleBlackboard initializer;
    private boolean crucial;
    private boolean setup;
    private boolean run;
    
    protected final static Logger logger = Logger.getLogger(
            AbstractPollyModule.class.getName());
    
    
    public AbstractPollyModule(String name, ModuleBlackboard initializer) {
        this(name, initializer, false);
    }
    
    
    public AbstractPollyModule(String name, 
                ModuleBlackboard initializer, boolean crucial) {
        this.name = name;
        this.initializer = initializer;
        this.crucial = crucial;
    }


    public final boolean setup() {
        if (this.setup) {
            return true;
        }
        //logger.debug("Requiring modules for module'" + this.name + "'");
        this.require();
        
        try {
            logger.info("Setting up module '" + this.name + "'");
            this.setup = this.doSetup();
            return this.setup;
        } catch (Exception e) {
            logger.error("Error while setup of module '" + this.name + "'", e);
            this.handleSetupException(e);
        }
        return false;
    }
    
    

    public final void run() {
        if (this.run) {
            return;
        }
        
        try {
            logger.info("Running module '" + this.name + "'");
            this.doRun();
        } catch (Exception e) {
            logger.error("Runtime error in module '" + this.name + "'", e);
            this.handleRuntimeException(e);
        } finally {
            this.run = true;
        }
    }
    
    
    
    public <E> E requireComponent(Class<E> clazz) {
        //logger.trace("Module '" + this.name + "' requires '" + clazz.getName() + "'");
        return this.initializer.requireComponent(clazz);
    }
    
    
    
    public <E> void provideComponent(Class<E> clazz, E component) {
        //logger.trace("Component '" + this.name + "' provided '" + clazz.getName() + "'");
        this.initializer.provideComponent(clazz, component);
    }
    
    
    
    public <E> boolean isProvided(Class<E> clazz) {
        return this.initializer.isProvided(clazz);
    }
    
    
    
    
    public boolean isCrucial() {
        return this.crucial;
    }
    
    
    
    public boolean isSetup() {
        return this.setup;
    }
    
    
    
    public boolean isRun() {
        return this.run;
    }
    
    
    public abstract boolean doSetup() throws Exception;
    
    public abstract void doRun() throws Exception;
    
    public abstract void require();
    
    public void handleRuntimeException(Exception e) {}
    
    public void handleSetupException(Exception e) {}
    
    
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        }
        
        if (other instanceof AbstractPollyModule) {
            AbstractPollyModule o = (AbstractPollyModule) other;
            return o.name.equals(this.name);
        }
        return false;
    }

}
