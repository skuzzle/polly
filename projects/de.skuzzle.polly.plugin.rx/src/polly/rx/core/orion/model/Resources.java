package polly.rx.core.orion.model;

import java.util.Map;

import de.skuzzle.polly.tools.Equatable;
import polly.rx.entities.RxRessource;

public interface Resources extends Equatable {
    
    public Map<RxRessource, ? extends Number> asMap();

    public abstract Number getAmount(RxRessource ress);

    public abstract Number[] getAmountArray();
}