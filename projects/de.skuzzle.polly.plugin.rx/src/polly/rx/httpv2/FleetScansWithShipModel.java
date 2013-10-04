package polly.rx.httpv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpEvent;
import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScan;


public class FleetScansWithShipModel extends FleetScanTableModel {

    
    public FleetScansWithShipModel(FleetDBManager fleetDb) {
        super(fleetDb);
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
    }

    
    
    public Map<String, String> getRequestParameters(HttpEvent e) {
        final Map<String, String> result = new HashMap<>();
        result.put("SHIP_ID", e.get("SHIP_ID"));
        return result;
    }
    
    
    
    @Override
    public List<FleetScan> getData(HttpEvent e) {
        final int ship = Integer.parseInt(e.get("SHIP_ID"));
        return this.fleetDb.getScanWithShip(ship);
    }
}
