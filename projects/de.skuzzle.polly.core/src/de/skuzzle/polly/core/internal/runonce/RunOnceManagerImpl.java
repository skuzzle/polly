package de.skuzzle.polly.core.internal.runonce;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.RunOnceManager;


public class RunOnceManagerImpl implements RunOnceManager {

    private final static Logger logger = Logger.getLogger(RunOnceManagerImpl.class
        .getName());
    
    private final Collection<Runnable> actions;
    private final Configuration runOnceCfg;
    
    
    
    public RunOnceManagerImpl(Configuration runOnceCfg) {
        this.runOnceCfg = runOnceCfg;
        this.actions = new ArrayList<Runnable>();
    }
    
    
    
    @Override
    public synchronized void registerAction(Runnable r) {
        final boolean b = this.runOnceCfg.readBoolean(r.getClass().getName());
        if (b) {
            logger.info("Ignoring action because it has already been executed: " + 
                r.getClass().getName());
            return;
        }
        this.actions.add(r);
    }

    
    
    public void runActions() {
        for (final Runnable r : this.actions) {
            try {
                logger.info("Running run-once action: " + r.getClass());
                r.run();
                this.runOnceCfg.setProperty(r.getClass().getName(), Boolean.TRUE);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }
}
