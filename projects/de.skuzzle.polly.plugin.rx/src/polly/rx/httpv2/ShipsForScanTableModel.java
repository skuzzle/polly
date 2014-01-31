package polly.rx.httpv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpEvent;
import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;


public class ShipsForScanTableModel extends FleetScanShipTableModel {

    private final static String SCAN_ID = "SCAN_ID"; //$NON-NLS-1$
    
    public ShipsForScanTableModel(FleetDBManager fleetDb) {
        super(fleetDb);
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
    }

    
    
    public Map<String, String> getRequestParameters(HttpEvent e) {
        final Map<String, String> result = new HashMap<>();
        result.put(SCAN_ID, e.get(SCAN_ID));
        return result;
    }
    
    
    
    @Override
    public List<FleetScanShip> getData(HttpEvent e) {
        final int scanId = Integer.parseInt(e.get(SCAN_ID));
        final FleetScan fs = this.fleetDB.getScanById(scanId);
        return fs.getShips();
    }
    
    
    
    @Override
    public String getRefreshKey() {
        return SCAN_ID;
    }
}
