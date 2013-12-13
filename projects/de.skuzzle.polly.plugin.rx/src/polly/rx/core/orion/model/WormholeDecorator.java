package polly.rx.core.orion.model;

import java.util.Date;


public class WormholeDecorator implements Wormhole {

    private final Wormhole wrapped;
    
    public WormholeDecorator(Wormhole wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public String getName() {
        return this.wrapped.getName();
    }

    @Override
    public Date getDate() {
        return this.wrapped.getDate();
    }

    @Override
    public int getMinUnload() {
        return this.wrapped.getMinUnload();
    }

    @Override
    public int getMaxUnload() {
        return this.wrapped.getMaxUnload();
    }

    @Override
    public Sector getTarget() {
        return this.wrapped.getTarget();
    }

    @Override
    public Sector getSource() {
        return this.wrapped.getSource();
    }

    @Override
    public LoadRequired requiresLoad() {
        return this.wrapped.requiresLoad();
    }
}
