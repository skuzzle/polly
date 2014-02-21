package polly.rx.core.orion.model;



public interface ReportShip extends Ship {
    
    public String getCapiName();
    
    public int getCapiXp();
    
    public int getCrewXp();

    public ShipStats getStats();
    
    public ShipStats getDamage();
}