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
import polly.rx.commands.RouteCommand;
import polly.rx.commands.VenadCommand;
import polly.rx.core.AZEntryManager;
import polly.rx.core.FleetDBManager;
import polly.rx.core.ScoreBoardManager;
import polly.rx.core.TrainManagerV2;
import polly.rx.core.orion.FleetTracker;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.WormholeProvider;
import polly.rx.core.orion.datasource.DBOrionAccess;
import polly.rx.core.orion.datasource.MemoryFleetTracker;
import polly.rx.core.orion.datasource.WLSWormholeProvider;
import polly.rx.core.orion.http.OrionController;
import polly.rx.core.orion.http.OrionNewsProvider;
import polly.rx.entities.AZEntry;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.DBPortal;
import polly.rx.entities.DBProduction;
import polly.rx.entities.DBQuadrant;
import polly.rx.entities.DBSector;
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
import polly.rx.httpv2.OpenTrainingsModel;
import polly.rx.httpv2.RXController;
import polly.rx.httpv2.ScoreboardDetailModel;
import polly.rx.httpv2.ScoreboardTableModel;
import polly.rx.httpv2.ShipsForScanTableModel;
import polly.rx.httpv2.StatisticsGatherer;
import polly.rx.httpv2.TrainingTableModel;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.Types;
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

    public final static String FLEET_MANAGER_ROLE = "polly.roles.FLEET_MANAGER"; //$NON-NLS-1$
    public final static String TRAINER_ROLE       = "polly.roles.TRAINER"; //$NON-NLS-1$
    public final static String SBE_MANAGER_ROLE   = "polly.roles.SBE_MANAGER"; //$NON-NLS-1$
    public final static String ORION_ROLE         = "polly.roles.ORION"; //$NON-NLS-1$
    
    public final static String RESSOURCES_PERMISSION             = "polly.permission.RESSOURCES"; //$NON-NLS-1$
    public final static String ADD_TRAIN_PERMISSION              = "polly.permission.ADD_TRAIN"; //$NON-NLS-1$
    public final static String DELIVER_TRAIN_PERMISSION          = "polly.permission.DELIVER_TRAIN"; //$NON-NLS-1$
    public final static String MYTRAINS_PERMISSION               = "polly.permission.MY_TRAINS"; //$NON-NLS-1$
    public final static String MY_VENAD_PERMISSION               = "polly.permission.MY_VENAD"; //$NON-NLS-1$
    public final static String CLOSE_TRAIN_PERMISSION            = "polly.permission.CLOSE_TRAIN"; //$NON-NLS-1$
    public final static String IP_PERMISSION                     = "polly.permission.IP"; //$NON-NLS-1$
    public final static String SBE_PERMISSION                    = "polly.permission.SBE"; //$NON-NLS-1$
    public final static String CRACKER_PERMISSION                = "polly.permission.CRACKER"; //$NON-NLS-1$
    public final static String RANK_PERMISSION                   = "polly.permission.RANK"; //$NON-NLS-1$
    public final static String VENAD    = "VENAD"; //$NON-NLS-1$
    public final static String CRACKER  = "CRACKER"; //$NON-NLS-1$
    public final static String MAX_MONTHS = "MAX_MONTHTS"; //$NON-NLS-1$
    public final static String AUTO_REMIND = "AUTO_REMIND"; //$NON-NLS-1$
    public final static String AUTO_REMIND_AZ = "AUTO_REMIND_AZ"; //$NON-NLS-1$
    public final static String LOW_PZ_WARNING = "LOW_PZ_WARNING"; //$NON-NLS-1$
    public final static String PORTALS = "PORTALS"; //$NON-NLS-1$
    private static final String VENAD_DEFAULT = "<unbekannt>"; //$NON-NLS-1$
    
    
    private FleetDBManager fleetDBManager;
    private TrainManagerV2 trainManager;
    private ScoreBoardManager sbeManager;
    private AZEntryManager azManager;
    
    
    public MyPlugin(MyPolly myPolly) 
                throws DuplicatedSignatureException, IncompatiblePluginException {
        super(myPolly);
        
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
        
        // orion
        myPolly.persistence().registerEntity(DBProduction.class);
        myPolly.persistence().registerEntity(DBSector.class);
        myPolly.persistence().registerEntity(DBQuadrant.class);
        myPolly.persistence().registerEntity(DBPortal.class);
        
        this.addCommand(new RankCommand(myPolly, this.sbeManager));
        
        
        myPolly.webInterface().addCategory(new MenuCategory(0, MSG.httpRxCategory));
        myPolly.webInterface().getServer().addController(
                new RXController(myPolly, fleetDBManager, sbeManager, azManager));
        
        
        final HTMLTableModel<FleetScan> scanModel = new FleetScanTableModel(fleetDBManager);
        final HTMLTable<FleetScan> fleetScanTable = new HTMLTable<FleetScan>("fleetScans", scanModel, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<FleetScanShip> scanShipModel = new FleetScanShipTableModel(fleetDBManager);
        final HTMLTable<FleetScanShip> fleetScanShipTable = new HTMLTable<FleetScanShip>("ships", scanShipModel, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<FleetScan> scansWithShip = new FleetScansWithShipModel(fleetDBManager);
        final HTMLTable<FleetScan> scansWithShipTable = new HTMLTable<FleetScan>("scansWithShip", scansWithShip, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<FleetScanShip> shipsForScanModel = new ShipsForScanTableModel(fleetDBManager);
        final HTMLTable<FleetScanShip> shipsForScanTable = new HTMLTable<FleetScanShip>("ships", shipsForScanModel, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<ScoreBoardEntry> scoreboard = new ScoreboardTableModel(sbeManager);
        final HTMLTable<ScoreBoardEntry> scoreboardTable = new HTMLTable<ScoreBoardEntry>("scoreboard", scoreboard, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<ScoreBoardEntry> scoreboardDetail = new ScoreboardDetailModel(sbeManager);
        final HTMLTable<ScoreBoardEntry> scoreboardDetailTable = new HTMLTable<>("entries", scoreboardDetail, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<BattleReport> reportModel = new BattleReportModel(fleetDBManager);
        final HTMLTable<BattleReport> reportTabble = new HTMLTable<BattleReport>("reports", reportModel, myPolly); //$NON-NLS-1$
        final StatisticsGatherer statsGatherer = new StatisticsGatherer();
        reportTabble.addModelListener(statsGatherer);
        
        
        final HTMLTableModel<TrainEntityV3> trainingModel = new TrainingTableModel(trainManager);
        final HTMLTable<TrainEntityV3> trainTable = new HTMLTable<>("closedTrainings", trainingModel, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<TrainEntityV3> openTrainingModel = new OpenTrainingsModel(trainManager);
        final HTMLTable<TrainEntityV3> openTrainTable = new HTMLTable<>("openTrainings", openTrainingModel, myPolly); //$NON-NLS-1$
        
        final HTMLTableModel<BattleReportShip> reportAttackerShipModel = new BattleReportShipModel(fleetDBManager, true);
        final HTMLTableModel<BattleReportShip> reportDefenderShipModel = new BattleReportShipModel(fleetDBManager, false);
        final HTMLTable<BattleReportShip> reportAttackerShipTable = new HTMLTable<>("attackerShips", reportAttackerShipModel, myPolly); //$NON-NLS-1$
        final HTMLTable<BattleReportShip> reportDefenderShipTable = new HTMLTable<>("defenderShips", reportDefenderShipModel, myPolly); //$NON-NLS-1$
        
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allFleetScans", fleetScanTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allFleetScanShips", fleetScanShipTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scansWithShip", scansWithShipTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/shipsForScan", shipsForScanTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scoreboard", scoreboardTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scoreboardDetail", scoreboardDetailTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allReports", reportTabble); //$NON-NLS-1$
        
        myPolly.webInterface().getServer().addHttpEventHandler("/api/reportAttackerShips", reportAttackerShipTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/reportDefenderShips", reportDefenderShipTable); //$NON-NLS-1$
        
        myPolly.webInterface().getServer().addHttpEventHandler("/api/closedTrainings", trainTable); //$NON-N //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/openTrainings", openTrainTable); //$NON-N //$NON-NLS-1$
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
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
        result.add(OrionController.VIEW_ORION_PREMISSION);
        result.add(OrionController.WRITE_ORION_PREMISSION);
        result.add(OrionController.ROUTE_ORION_PREMISSION);
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
        
        roleManager.createRole(ORION_ROLE);
        roleManager.assignPermission(ORION_ROLE, OrionController.VIEW_ORION_PREMISSION);
        roleManager.assignPermission(ORION_ROLE, OrionController.WRITE_ORION_PREMISSION);
        roleManager.assignPermission(ORION_ROLE, OrionController.ROUTE_ORION_PREMISSION);
        
        super.assignPermissions(roleManager);
    }

    
    
    @Override
    public void onLoad() throws PluginException {
        
        // ORION
        final DBOrionAccess dboa = new DBOrionAccess(this.getMyPolly().persistence());
        final WormholeProvider holeProvider = new WLSWormholeProvider();
        final FleetTracker tracker = new MemoryFleetTracker();
        
        Orion.initialize(
                dboa.getQuadrantProvider(), 
                dboa.getQuadrantUpdater(), 
                holeProvider, 
                dboa.getPortalProvider(),
                dboa.getPortalUpdater(),
                tracker
            );
        
        final OrionController oc = new OrionController(this.getMyPolly(), azManager);
        final OrionNewsProvider newsProvider = new OrionNewsProvider(
                Orion.INSTANCE.getFleetTracker(), 
                Orion.INSTANCE.getPortalUpdater());
        this.getMyPolly().webInterface().getServer().addController(oc);
        this.getMyPolly().webInterface().getServer().addHttpEventHandler(
                OrionNewsProvider.NEWS_URL, newsProvider);
        
        try {
            this.addCommand(new RouteCommand(this.getMyPolly()));
        } catch (DuplicatedSignatureException e1) {
            e1.printStackTrace();
        }
        
        
        try {
            this.getMyPolly().users().addAttribute(VENAD, 
                new Types.StringType(VENAD_DEFAULT), 
                MSG.pluginVenadDesc, 
                MSG.httpRxCategory);
            this.getMyPolly().users().addAttribute(CRACKER, new Types.NumberType(0.0),
                MSG.pluginCrackerDesc, 
                MSG.httpRxCategory, Constraints.INTEGER);
            this.getMyPolly().users().addAttribute(AUTO_REMIND, 
                    new Types.BooleanType(false), 
                    MSG.pluginAutoRemindDesc, 
                    MSG.httpRxCategory);
            this.getMyPolly().users().addAttribute(AUTO_REMIND_AZ, 
                    new Types.TimespanType(840), MSG.pluginAutoRemindAzDesc, 
                    MSG.httpRxCategory, Constraints.TIMESPAN);
            this.getMyPolly().users().addAttribute(LOW_PZ_WARNING, 
                    new Types.NumberType(0.0), 
                    MSG.pluginLowPzWarningDesc, 
                    MSG.httpRxCategory, Constraints.INTEGER);
            
            this.getMyPolly().users().addAttribute(PORTALS, new Types.StringType(""),  //$NON-NLS-1$
                    MSG.pluginPortalDesc, MSG.httpRxCategory, 
                    new SectorListAttributeConstraint());
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
