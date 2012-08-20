package polly.rx.http;

import polly.rx.FleetDBManager;
import polly.rx.entities.FleetScan;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class FleetScanInfoHttpAction extends HttpAction {

    private FleetDBManager fleetDBManager;

    public FleetScanInfoHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/fleetscan_info", myPolly);
        this.fleetDBManager = fleetDBManager;
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext c = new HttpTemplateContext("pages/fleetscan_info.html");
        
        int scanId = Integer.parseInt(e.getProperty("id"));
        
        FleetScan scan = this.fleetDBManager.getScanById(scanId);
        
        c.put("scan", scan);
        return c;
    }

}
