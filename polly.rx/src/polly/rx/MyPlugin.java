package polly.rx;

import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanHistoryEntry;
import polly.rx.entities.FleetScanShip;
import polly.rx.http.BattleReportHttpAction;
import polly.rx.http.BattleReportInfosHttpAction;
import polly.rx.http.FleetScanHttpAction;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;


public class MyPlugin extends PollyPlugin {

    
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
        myPolly.web().addHttpAction(new BattleReportHttpAction(
            myPolly, this.fleetDBManager));
        myPolly.web().addMenuUrl("Revorix", "Kampfberichte");
        
        
        myPolly.web().addMenuUrl("Revorix", "FleetScans");
        myPolly.web().addHttpAction(new FleetScanHttpAction(myPolly, this.fleetDBManager));
    }

    
    
    @Override
    public void onLoad() throws PluginException {
        super.onLoad();
    }
}
