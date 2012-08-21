package polly.rx.http;

import java.util.List;

import polly.rx.FleetDBManager;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;


public class QueryOwnerHttpAction extends HttpAction {

    private FleetDBManager fleetDBManager;


    public QueryOwnerHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/query_scans", myPolly);
        this.fleetDBManager = fleetDBManager;
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext c = new HttpTemplateContext("pages/query_scans.html");
        
        String query = e.getProperty("query");
        String method = e.getProperty("method") == null
            ? "owner"
            : e.getProperty("method");
        
        List<FleetScanShip> ships = null; 
        List<FleetScan> scans = null; 
        
        if (method.equals("owner")) {
            scans = this.fleetDBManager.getScansWithOwner(query);
            ships = this.fleetDBManager.getShipsByOwner(query);
        } else {
            scans = this.fleetDBManager.getScansWithClan(query);
            ships = this.fleetDBManager.getShipsByClan(query);
        }
        
        c.put("fleetShips", ships);
        c.put("fleetScans", scans);
        c.put("query", query);
        c.put("method", method);
        
        HttpTemplateSortHelper.makeListSortable(c, e, "scanSortKey", "scanDesc", "getDate");
        HttpTemplateSortHelper.makeListSortable(c, e, "shipSortKey", "shipDesc", "getRxId");
        
        return c;
    }

}
