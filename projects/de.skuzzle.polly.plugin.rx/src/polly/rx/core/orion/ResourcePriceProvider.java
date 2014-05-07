package polly.rx.core.orion;

import java.util.Date;
import java.util.List;

import polly.rx.core.orion.model.Production;
import polly.rx.entities.RxRessource;


public interface ResourcePriceProvider {
    
    public Date getRefreshTime();

    public double getPrice(RxRessource resource);
    
    public double getPrice(RxRessource resource, Date time);
    
    public List<? extends Production> getAllPrices();
    
    public List<? extends Production> getAllPrices(Date time);
}
