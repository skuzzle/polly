package polly.rx;

import java.util.Set;
import java.util.TreeSet;

import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanHistoryEntry;
import polly.rx.entities.FleetScanShip;
import polly.rx.http.BattleReportInfosHttpAction;
import polly.rx.http.FleetScanInfoHttpAction;
import polly.rx.http.FleetShipInfoHttpAction;
import polly.rx.http.FleetScanHttpAction;
import polly.rx.http.QueryOwnerHttpAction;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class MyPlugin extends PollyPlugin {

    public final static String FLEET_MANAGER_ROLE = "polly.roles.FLEET_MANAGER";
    
    private FleetDBManager fleetDBManager;
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
        super(myPolly);
        
        this.fleetDBManager = new FleetDBManager(myPolly.persistence());
        
        myPolly.persistence().registerEntity(BattleReport.class);
        myPolly.persistence().registerEntity(BattleReportShip.class);
        myPolly.persistence().registerEntity(BattleDrop.class);
        myPolly.persistence().registerEntity(FleetScan.class);
        myPolly.persistence().registerEntity(FleetScanHistoryEntry.class);
        myPolly.persistence().registerEntity(FleetScanShip.class);
        
        myPolly.web().addHttpAction(new BattleReportInfosHttpAction(
            myPolly, this.fleetDBManager));
        /*myPolly.web().addHttpAction(new BattleReportHttpAction(
            myPolly, this.fleetDBManager));
        myPolly.web().addMenuUrl("Revorix", "Kampfberichte");*/
        
        
        myPolly.web().addMenuUrl("Revorix", "FleetScans");
        myPolly.web().addHttpAction(new FleetScanHttpAction(myPolly, this.fleetDBManager));
        myPolly.web().addHttpAction(new FleetShipInfoHttpAction(myPolly, this.fleetDBManager));
        myPolly.web().addHttpAction(new FleetScanInfoHttpAction(myPolly, this.fleetDBManager));
        myPolly.web().addHttpAction(new QueryOwnerHttpAction(myPolly, this.fleetDBManager));
    }
    
    
    
    @Override
    public Set<String> getContainedPermissions() {
        Set<String> result = new TreeSet<String>();
        result.add(FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        result.add(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        result.add(FleetDBManager.DELETE_BATTLE_REPORT_PERMISSION);
        result.add(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        result.add(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        result.add(FleetDBManager.DELETE_FLEET_SCAN_PERMISSION);
        return result;
    }
    
    
    
    @Override
    public void assignPermissions(RoleManager roleManager)
            throws RoleException, DatabaseException {
        
        roleManager.createRole(FLEET_MANAGER_ROLE);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.DELETE_BATTLE_REPORT_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.DELETE_BATTLE_REPORT_PERMISSION);
        
        roleManager.assignPermission(RoleManager.DEFAULT_ROLE, FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        roleManager.assignPermission(RoleManager.DEFAULT_ROLE, FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        
        super.assignPermissions(roleManager);
    }

    
    
    @Override
    public void onLoad() throws PluginException {
        super.onLoad();
    }
}
