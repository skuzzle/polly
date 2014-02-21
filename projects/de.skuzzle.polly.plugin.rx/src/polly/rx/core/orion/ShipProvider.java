package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.FleetScan;
import polly.rx.core.orion.model.ReportShip;
import polly.rx.core.orion.model.ScanShip;
import polly.rx.core.orion.model.Ship;


public interface ShipProvider {

    public ScanShip findLatestScanShip(Ship ship);
    
    public List<? extends ScanShip> findShipHistory(Ship ship);
    
    public List<? extends ScanShip> findShipsByOwner(String venadName);
    
    public List<? extends ScanShip> findAllScannedShips();
    
    public List<? extends FleetScan> findAllScans();
    
    public ReportShip findLatestReportShip();
}