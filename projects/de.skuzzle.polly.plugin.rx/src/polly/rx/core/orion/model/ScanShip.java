package polly.rx.core.orion.model;



public interface ScanShip extends Ship {
    
    public HistoryType getHistoryType();
    
    public int getTechlevel();
    
    public int getSens();
    
    public Sector getSector();
}
