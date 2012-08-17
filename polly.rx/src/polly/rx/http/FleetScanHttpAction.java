package polly.rx.http;

import java.util.List;

import polly.rx.FleetDBManager;
import polly.rx.ParseException;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;
import polly.rx.parsing.FleetScanParser;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class FleetScanHttpAction extends HttpAction {

    private FleetDBManager fleetDBManager;

    public FleetScanHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/FleetScans", myPolly);
        this.fleetDBManager = fleetDBManager;
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext c = new HttpTemplateContext("pages/fleetscans.html");
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("postScan")) {
            String paste = e.getProperty("paste");
            String metaData = e.getProperty("metaData") == null
                ? ""
                : e.getProperty("metaData");
            String quadrant = e.getProperty("quadrant") == null 
                ? "Unbekannt" 
                : e.getProperty("quadrant");
            int x = Integer.parseInt(e.getProperty("x"));
            int y = Integer.parseInt(e.getProperty("y"));
            
            
            try {
                FleetScan scan = FleetScanParser.parseFleetScan(paste, 
                    quadrant, x, y, metaData);
                this.fleetDBManager.addFleetScan(scan);
            } catch (ParseException e1) {
                e.throwTemplateException(e1);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        }
        
        List<FleetScan> allScans = this.fleetDBManager.getAllScans();
        List<FleetScanShip> allShips = this.fleetDBManager.getAllScannedShips();
        
        c.put("allScans", allScans);
        c.put("allShips", allShips);
        
        return c;
    }

}
