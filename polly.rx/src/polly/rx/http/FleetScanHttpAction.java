package polly.rx.http;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;
import polly.rx.parsing.FleetScanParser;
import polly.rx.parsing.ParseException;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class FleetScanHttpAction extends HttpAction {

    private FleetDBManager fleetDBManager;

    public FleetScanHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/FleetScans", myPolly);
        this.fleetDBManager = fleetDBManager;
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) 
            throws HttpTemplateException, InsufficientRightsException {
        HttpTemplateContext c = new HttpTemplateContext("pages/fleetscans.html");
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("postScan")) {
            if (!this.getMyPolly().roles().hasPermission(
                e.getSession().getUser(), FleetDBManager.ADD_FLEET_SCAN_PERMISSION)) {
                throw new InsufficientRightsException(this);
            }
            
            String paste = e.getSource().escapeHtml(e.getProperty("paste"));
            String metaData = e.getProperty("metaData") == null
                ? ""
                : e.getSource().escapeHtml(e.getProperty("metaData"));
            
            String quadrant = e.getProperty("quadrant") == null 
                ? "Unbekannt" 
                : e.getSource().escapeHtml(e.getProperty("quadrant"));
            
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
        } else if (action != null && action.equals("delete")) {
            if (!this.getMyPolly().roles().hasPermission(
                e.getSession().getUser(), FleetDBManager.DELETE_FLEET_SCAN_PERMISSION)) {
                throw new InsufficientRightsException(this);
            }
            
            int id = Integer.parseInt(e.getProperty("id"));
            
            try {
                this.fleetDBManager.deleteFleetScan(id);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        }
        
        HttpTemplateSortHelper.makeListSortable(c, e, "scanSortKey", "scanDesc", "getDate");
        HttpTemplateSortHelper.makeListSortable(c, e, "shipSortKey", "shipDesc", "getRxId");
        
        List<FleetScan> allScans = this.fleetDBManager.getAllScans();
        List<FleetScanShip> allShips = this.fleetDBManager.getAllScannedShips();
        
        Set<String> clans = new TreeSet<String>();
        Set<String> venads = new TreeSet<String>();
        Set<String> allQuads = new TreeSet<String>();
            
        for (FleetScan scan : allScans) {
            clans.add(scan.getOwnerClan());
            venads.add(scan.getOwnerName());
            allQuads.add(scan.getQuadrant());
        }
        for (FleetScanShip ship : allShips) {
            clans.add(ship.getOwnerClan());
            venads.add(ship.getOwner());
            allQuads.add(ship.getQuadrant());
        }
        
        c.put("allClans", clans);
        c.put("allVenads", venads);
        c.put("allQuads", allQuads);
        c.put("allScans", allScans);
        c.put("allShips", allShips);
        
        return c;
    }

}
