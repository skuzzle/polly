package polly.rx.core.orion;

import java.util.Date;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Sector;

public class PortalDecorator implements Portal {

    private final Portal wrapped;



    public PortalDecorator(Portal wrapped) {
        Check.objects(wrapped).notNull();
        this.wrapped = wrapped;
    }



    @Override
    public String toString() {
        return this.wrapped.toString();
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
        return this.wrapped.getEquivalenceClass();
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return this.wrapped.actualEquals(o);
    }



    @Override
    public Sector getSector() {
        return this.wrapped.getSector();
    }



    @Override
    public String getOwnerName() {
        return this.wrapped.getOwnerName();
    }



    @Override
    public String getOwnerClan() {
        return this.wrapped.getOwnerClan();
    }



    @Override
    public PortalType getType() {
        return this.wrapped.getType();
    }



    @Override
    public Date getDate() {
        return this.wrapped.getDate();
    }
}
