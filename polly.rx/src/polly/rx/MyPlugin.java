package polly.rx;

import java.util.Set;
import java.util.TreeSet;

import polly.rx.commands.AddTrainCommand;
import polly.rx.commands.CloseTrainCommand;
import polly.rx.commands.DeliverTrainCommand;
import polly.rx.commands.MyTrainsCommand;
import polly.rx.commands.MyVenadCommand;
import polly.rx.commands.VenadCommand;
import polly.rx.core.FleetDBManager;
import polly.rx.core.TrainManagerV2;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanHistoryEntry;
import polly.rx.entities.FleetScanShip;
import polly.rx.entities.TrainEntity;
import polly.rx.entities.TrainEntityV2;
import polly.rx.http.BattleReportHttpAction;
import polly.rx.http.BattleReportInfosHttpAction;
import polly.rx.http.FleetScanInfoHttpAction;
import polly.rx.http.FleetShipInfoHttpAction;
import polly.rx.http.FleetScanHttpAction;
import polly.rx.http.MyTrainsHttpAction;
import polly.rx.http.QueryOwnerHttpAction;
import polly.rx.http.TrainerHttpAction;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.constraints.Constraints;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class MyPlugin extends PollyPlugin {

    public final static String FLEET_MANAGER_ROLE = "polly.roles.FLEET_MANAGER";
    public final static String TRAINER = "polly.roles.TRAINER";
    public final static String ADD_TRAIN_PERMISSION              = "polly.permission.ADD_TRAIN";
    public final static String DELIVER_TRAIN_PERMISSION          = "polly.permission.DELIVER_TRAIN";
    public final static String MYTRAINS_PERMISSION               = "polly.permission.MY_TRAINS";
    public final static String MY_VENAD_PERMISSION               = "polly.permission.MY_VENAD";
    public final static String CLOSE_TRAIN_PERMISSION            = "polly.permission.CLOSE_TRAIN";
    
    public final static String VENAD    = "VENAD";
    
    private FleetDBManager fleetDBManager;
    private TrainManagerV2 trainManager;
    
    
    public MyPlugin(MyPolly myPolly) 
                throws DuplicatedSignatureException, IncompatiblePluginException {
        super(myPolly);
        
        /* capi train related */
        this.trainManager = new TrainManagerV2(myPolly);
        this.getMyPolly().persistence().registerEntity(TrainEntity.class);
        this.getMyPolly().persistence().registerEntity(TrainEntityV2.class);
        
        this.addCommand(new AddTrainCommand(myPolly, this.trainManager));
        this.addCommand(new CloseTrainCommand(myPolly, this.trainManager));
        this.addCommand(new MyTrainsCommand(myPolly, this.trainManager));
        this.addCommand(new DeliverTrainCommand(myPolly, this.trainManager));
        this.addCommand(new VenadCommand(myPolly));
        this.addCommand(new MyVenadCommand(myPolly));
        
        
        myPolly.web().addMenuUrl("Revorix", "MyTrains");
        myPolly.web().addMenuUrl("Revorix", "Trainer");
        myPolly.web().addHttpAction(new MyTrainsHttpAction(myPolly, this.trainManager));
        myPolly.web().addHttpAction(new TrainerHttpAction(myPolly, this.trainManager));
        
        /* fleet db related */
        
        
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
        result.addAll(super.getContainedPermissions());
        return result;
    }
    
    
    
    @Override
    public void assignPermissions(RoleManager roleManager)
            throws RoleException, DatabaseException {
        
        roleManager.createRole(TRAINER);
        roleManager.assignPermission(TRAINER, ADD_TRAIN_PERMISSION);
        roleManager.assignPermission(TRAINER, CLOSE_TRAIN_PERMISSION);
        roleManager.assignPermission(TRAINER, DELIVER_TRAIN_PERMISSION);
        
        roleManager.assignPermission(RoleManager.DEFAULT_ROLE, MYTRAINS_PERMISSION);
        
        roleManager.createRole(FLEET_MANAGER_ROLE);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        
        super.assignPermissions(roleManager);
    }

    
    
    @Override
    public void onLoad() throws PluginException {
        try {
            this.getMyPolly().users().addAttribute(VENAD, "<unbekannt>");
            this.getMyPolly().users().addAttribute("AZ", "0", Constraints.INTEGER);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}
