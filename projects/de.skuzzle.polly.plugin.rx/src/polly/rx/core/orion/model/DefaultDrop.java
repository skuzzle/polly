package polly.rx.core.orion.model;

import java.util.Map;

import polly.rx.entities.RxRessource;
import de.skuzzle.polly.tools.Equatable;

public class DefaultDrop extends DefaultResources implements Drop {

    private final boolean hasArtifact;



    public DefaultDrop(Map<RxRessource, Integer> drop, boolean hasArtifact) {
        super(drop);
        this.hasArtifact = hasArtifact;
    }



    public DefaultDrop(Drop other) {
        super(other);
        this.hasArtifact = other.hasArtifact();
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.dropHash(this);
    }



    @Override
    public String toString() {
        return OrionObjectUtil.dropString(this);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Drop.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.dropEquals(this, (Drop) o);
    }



    @Override
    public boolean hasArtifact() {
        return this.hasArtifact;
    }
}
