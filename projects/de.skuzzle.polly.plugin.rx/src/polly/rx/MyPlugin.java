package polly.rx;

import java.util.Set;
import java.util.TreeSet;

import polly.rx.commands.AddTrainCommand;
import polly.rx.commands.CloseTrainCommand;
import polly.rx.commands.CrackerCommand;
import polly.rx.commands.DeliverTrainCommand;
import polly.rx.commands.IPCommand;
import polly.rx.commands.MyTrainsCommand;
import polly.rx.commands.MyVenadCommand;
import polly.rx.commands.RankCommand;
import polly.rx.commands.RessComand;
import polly.rx.commands.VenadCommand;
import polly.rx.core.AZEntryManager;
import polly.rx.core.FleetDBManager;
import polly.rx.core.ScoreBoardManager;
import polly.rx.core.TrainManagerV2;
import polly.rx.entities.AZEntry;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanHistoryEntry;
import polly.rx.entities.FleetScanShip;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.entities.TrainEntityV3;
import polly.rx.httpv2.BattleReportModel;
import polly.rx.httpv2.BattleReportShipModel;
import polly.rx.httpv2.FleetScanShipTableModel;
import polly.rx.httpv2.FleetScanTableModel;
import polly.rx.httpv2.FleetScansWithShipModel;
import polly.rx.httpv2.RXController;
import polly.rx.httpv2.ScoreboardDetailModel;
import polly.rx.httpv2.ScoreboardTableModel;
import polly.rx.httpv2.ShipsForScanTableModel;
import polly.rx.httpv2.StatisticsGatherer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.constraints.AttributeConstraint;
import de.skuzzle.polly.sdk.constraints.Constraints;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.httpv2.MenuCategory;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTable;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTableModel;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class MyPlugin extends PollyPlugin {

    public final static String FLEET_MANAGER_ROLE = "polly.roles.FLEET_MANAGER";
    public final static String TRAINER_ROLE       = "polly.roles.TRAINER";
    public final static String SBE_MANAGER_ROLE   = "polly.roles.SBE_MANAGER";
    
    public final static String RESSOURCES_PERMISSION             = "polly.permission.RESSOURCES";
    public final static String ADD_TRAIN_PERMISSION              = "polly.permission.ADD_TRAIN";
    public final static String DELIVER_TRAIN_PERMISSION          = "polly.permission.DELIVER_TRAIN";
    public final static String MYTRAINS_PERMISSION               = "polly.permission.MY_TRAINS";
    public final static String MY_VENAD_PERMISSION               = "polly.permission.MY_VENAD";
    public final static String CLOSE_TRAIN_PERMISSION            = "polly.permission.CLOSE_TRAIN";
    public final static String IP_PERMISSION                     = "polly.permission.IP";
    public final static String SBE_PERMISSION                    = "polly.permission.SBE";
    public final static String CRACKER_PERMISSION                = "polly.permission.CRACKER";
    public final static String RANK_PERMISSION                = "polly.permission.RANK";
    public final static String VENAD    = "VENAD";
    public final static String CRACKER  = "CRACKER";
    public final static String MAX_MONTHS = "MAX_MONTHTS";
    public final static String AUTO_REMIND = "AUTO_REMIND";
    public final static String AUTO_REMIND_AZ = "AUTO_REMIND_AZ";
    public final static String LOW_PZ_WARNING = "LOW_PZ_WARNING";
    
    
    private FleetDBManager fleetDBManager;
    private TrainManagerV2 trainManager;
    private DailyGreeter dailyGreeter;
    private ScoreBoardManager sbeManager;
    private AZEntryManager azManager;
    
    
    public MyPlugin(MyPolly myPolly) 
                throws DuplicatedSignatureException, IncompatiblePluginException {
        super(myPolly);
        
        this.dailyGreeter = new DailyGreeter();
        this.dailyGreeter.deploy(myPolly.irc());
        
        
        /* capi train related */
        this.trainManager = new TrainManagerV2(myPolly);
        this.getMyPolly().persistence().registerEntity(TrainEntityV3.class);
        
        this.addCommand(new AddTrainCommand(myPolly, this.trainManager));
        this.addCommand(new CloseTrainCommand(myPolly, this.trainManager));
        this.addCommand(new MyTrainsCommand(myPolly, this.trainManager));
        this.addCommand(new DeliverTrainCommand(myPolly, this.trainManager));
        this.addCommand(new VenadCommand(myPolly));
        this.addCommand(new MyVenadCommand(myPolly));
        this.addCommand(new IPCommand(myPolly));
        this.addCommand(new CrackerCommand(myPolly));
        this.addCommand(new RessComand(myPolly));
        
        /* fleet db related */
        
        
        this.fleetDBManager = new FleetDBManager(myPolly.persistence());
        this.sbeManager = new ScoreBoardManager(myPolly.persistence());
        this.azManager = new AZEntryManager(myPolly);
        
        myPolly.persistence().registerEntity(BattleReport.class);
        myPolly.persistence().registerEntity(BattleReportShip.class);
        myPolly.persistence().registerEntity(BattleDrop.class);
        myPolly.persistence().registerEntity(FleetScan.class);
        myPolly.persistence().registerEntity(FleetScanHistoryEntry.class);
        myPolly.persistence().registerEntity(FleetScanShip.class);
        myPolly.persistence().registerEntity(ScoreBoardEntry.class);
        myPolly.persistence().registerEntity(AZEntry.class);
        this.addCommand(new RankCommand(myPolly, this.sbeManager));
        
        
        myPolly.webInterface().addCategory(new MenuCategory(0, "Revorix"));
        myPolly.webInterface().getServer().addController(
                new RXController(myPolly, fleetDBManager, sbeManager, azManager));
        
        final HTMLTableModel<FleetScan> scanModel = new FleetScanTableModel(fleetDBManager);
        final HTMLTable<FleetScan> fleetScanTable = new HTMLTable<FleetScan>("fleetScans", scanModel, myPolly);
        
        final HTMLTableModel<FleetScanShip> scanShipModel = new FleetScanShipTableModel(fleetDBManager);
        final HTMLTable<FleetScanShip> fleetScanShipTable = new HTMLTable<FleetScanShip>("ships", scanShipModel, myPolly);
        
        final HTMLTableModel<FleetScan> scansWithShip = new FleetScansWithShipModel(fleetDBManager);
        final HTMLTable<FleetScan> scansWithShipTable = new HTMLTable<FleetScan>("scansWithShip", scansWithShip, myPolly);
        
        final HTMLTableModel<FleetScanShip> shipsForScanModel = new ShipsForScanTableModel(fleetDBManager);
        final HTMLTable<FleetScanShip> shipsForScanTable = new HTMLTable<FleetScanShip>("ships", shipsForScanModel, myPolly);
        
        final HTMLTableModel<ScoreBoardEntry> scoreboard = new ScoreboardTableModel(sbeManager);
        final HTMLTable<ScoreBoardEntry> scoreboardTable = new HTMLTable<ScoreBoardEntry>("scoreboard", scoreboard, myPolly);
        
        final HTMLTableModel<ScoreBoardEntry> scoreboardDetail = new ScoreboardDetailModel(sbeManager);
        final HTMLTable<ScoreBoardEntry> scoreboardDetailTable = new HTMLTable<>("entries", scoreboardDetail, myPolly);
        
        final HTMLTableModel<BattleReport> reportModel = new BattleReportModel(fleetDBManager);
        final HTMLTable<BattleReport> reportTabble = new HTMLTable<BattleReport>("reports", reportModel, myPolly);
        final StatisticsGatherer statsGatherer = new StatisticsGatherer();
        reportTabble.addModelListener(statsGatherer);
        
        
        final HTMLTableModel<BattleReportShip> reportAttackerShipModel = new BattleReportShipModel(fleetDBManager, true);
        final HTMLTableModel<BattleReportShip> reportDefenderShipModel = new BattleReportShipModel(fleetDBManager, false);
        final HTMLTable<BattleReportShip> reportAttackerShipTable = new HTMLTable<>("attackerShips", reportAttackerShipModel, myPolly);
        final HTMLTable<BattleReportShip> reportDefenderShipTable = new HTMLTable<>("defenderShips", reportDefenderShipModel, myPolly);
        
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allFleetScans", fleetScanTable);
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allFleetScanShips", fleetScanShipTable);
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scansWithShip", scansWithShipTable);
        myPolly.webInterface().getServer().addHttpEventHandler("/api/shipsForScan", shipsForScanTable);
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scoreboard", scoreboardTable);
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scoreboardDetail", scoreboardDetailTable);
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allReports", reportTabble);
        
        myPolly.webInterface().getServer().addHttpEventHandler("/api/reportAttackerShips", reportAttackerShipTable);
        myPolly.webInterface().getServer().addHttpEventHandler("/api/reportDefenderShips", reportDefenderShipTable);
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        this.dailyGreeter.undeploy(this.getMyPolly().irc());
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
        result.add(RANK_PERMISSION);
        result.add(RESSOURCES_PERMISSION);
        result.add(SBE_PERMISSION);
        result.add(CRACKER_PERMISSION);
        result.addAll(super.getContainedPermissions());
        return result;
    }
    
    
    
    @Override
    public void assignPermissions(RoleManager roleManager)
            throws RoleException, DatabaseException {
        
        roleManager.createRole(TRAINER_ROLE);
        roleManager.assignPermission(TRAINER_ROLE, ADD_TRAIN_PERMISSION);
        roleManager.assignPermission(TRAINER_ROLE, CLOSE_TRAIN_PERMISSION);
        roleManager.assignPermission(TRAINER_ROLE, DELIVER_TRAIN_PERMISSION);
        
        roleManager.assignPermission(RoleManager.DEFAULT_ROLE, MYTRAINS_PERMISSION);
        
        roleManager.createRole(FLEET_MANAGER_ROLE);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        roleManager.assignPermission(FLEET_MANAGER_ROLE, FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        
        roleManager.createRole(SBE_MANAGER_ROLE);
        roleManager.assignPermission(SBE_MANAGER_ROLE, SBE_PERMISSION);
        roleManager.assignPermission(SBE_MANAGER_ROLE, RANK_PERMISSION);
        
        roleManager.assignPermission(RoleManager.DEFAULT_ROLE, IP_PERMISSION);
        roleManager.assignPermission(RoleManager.DEFAULT_ROLE, CRACKER_PERMISSION);
        super.assignPermissions(roleManager);
    }

    
    
    @Override
    public void onLoad() throws PluginException {
        try {
            final String category = "Revorix";
            this.getMyPolly().users().addAttribute(VENAD, 
                new Types.StringType("<unbekannt>"), "Set your revorix Venad name", 
                category);
            this.getMyPolly().users().addAttribute(CRACKER, new Types.NumberType(0.0),
                "Your polly crackers. Do not edit this if you are fair!", category,
                Constraints.INTEGER);
            this.getMyPolly().users().addAttribute(MAX_MONTHS, 
                new Types.NumberType(24.0), 
                "Maximum number of months to display in scoreboard graphic", category, 
                new AttributeConstraint() {
                @Override
                public boolean accept(Types value) {
                    if (value instanceof NumberType) {
                        final NumberType nt = (NumberType) value;
                        return nt.isInteger() && 
                            nt.getValue() > 2.0 && 
                            nt.getValue() <= 24.0;
                    }
                    return false;
                }
            });
            this.getMyPolly().users().addAttribute(AUTO_REMIND, 
                    new Types.BooleanType(false), 
                    "When posting a live KB, polly will automatically set a remind for you", 
                    "Revorix");
            this.getMyPolly().users().addAttribute(AUTO_REMIND_AZ, 
                    new Types.TimespanType(840), "Revorix AZ time for auto reminds", 
                    "Revorix", Constraints.TIMESPAN);
            this.getMyPolly().users().addAttribute(LOW_PZ_WARNING, 
                    new Types.NumberType(0.0), 
                    "When using the live KB Greasemonkey script, your browser will show a "
                    + "warning if one ship's PZ drops below this value. Set to 0 to "
                    + "disable the warning", 
                    "Revorix", Constraints.INTEGER);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        
        try {
            this.fleetDBManager.cleanInvalidBattleReports();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}
