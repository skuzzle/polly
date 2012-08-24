package polly.rx.http;

import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;


public class FleetShipInfoHttpAction extends HttpAction {

    private FleetDBManager fleetDBManager;

    public FleetShipInfoHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/fleetship_info", myPolly);
        this.fleetDBManager = fleetDBManager;
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext c = new HttpTemplateContext("pages/fleetship_info.html");
        
        int rxId = Integer.parseInt(e.getProperty("shipId"));
        FleetScanShip ship = this.fleetDBManager.getShipByRevorixId(rxId);
        List<FleetScan> scans = this.fleetDBManager.getScanWithShip(rxId);
        HttpTemplateSortHelper.makeListSortable(
            c, e, "scanSortKey", "scanDesc", "getDate");
        
        c.put("ship", ship);
        c.put("scans", scans);
        
        return c;
    }

}
