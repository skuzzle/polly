package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;
import polly.rx.entities.RxRessource;


public interface Drop extends Equatable {

    public int getAmount(RxRessource ress);
    
    public Integer[] getAmountArray();
    
    public boolean hasArtifact();
}