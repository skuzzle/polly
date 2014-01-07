package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public class WormholeDecorator implements Wormhole {

    private final Wormhole wrapped;
    
    public WormholeDecorator(Wormhole wrapped) {
        if (wrapped == null) {
            throw new NullPointerException();
        }
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
    
    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.wrapped.equals(obj);
    }
    
    @Override
    public Class<?> getEquivalenceClass() {
        return wrapped.getEquivalenceClass();
    }

    @Override
    public boolean actualEquals(Equatable o) {
        return wrapped.actualEquals(o);
    }
    
    @Override
    public String toString() {
        return this.wrapped.toString();
    }
}
