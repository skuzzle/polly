package polly.rx;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.captcha.ImageDatabase;
import polly.rx.captcha.RxCaptchaKiller;
import polly.rx.commands.AddTrainCommand;
import polly.rx.commands.CloseTrainCommand;
import polly.rx.commands.CrackerCommand;
import polly.rx.commands.DeliverTrainCommand;
import polly.rx.commands.IGMCommand;
import polly.rx.commands.IPCommand;
import polly.rx.commands.MyTrainsCommand;
import polly.rx.commands.MyVenadCommand;
import polly.rx.commands.RankCommand;
import polly.rx.commands.RessComand;
import polly.rx.commands.RouteCommand;
import polly.rx.commands.UrlAnonymizationCommand;
import polly.rx.commands.VenadCommand;
import polly.rx.core.AZEntryManager;
import polly.rx.core.FleetDBManager;
import polly.rx.core.ScoreBoardManager;
import polly.rx.core.TrainManagerV2;
import polly.rx.core.orion.FleetTracker;
import polly.rx.core.orion.LoginCodeManager;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.OrionChatProvider;
import polly.rx.core.orion.ResourcePriceProvider;
import polly.rx.core.orion.VenadUserMapper;
import polly.rx.core.orion.WormholeProvider;
import polly.rx.core.orion.datasource.DBFleetHeatMap;
import polly.rx.core.orion.datasource.DBOrionAccess;
import polly.rx.core.orion.datasource.DBOrionChatProvider;
import polly.rx.core.orion.datasource.MemoryFleetTracker;
import polly.rx.core.orion.datasource.QZoneResourcePriceProvider;
import polly.rx.core.orion.datasource.WLSWormholeProvider;
import polly.rx.core.orion.http.OrionChatController;
import polly.rx.core.orion.http.OrionController;
import polly.rx.core.orion.http.OrionNewsProvider;
import polly.rx.core.orion.model.AlienRace;
import polly.rx.core.orion.model.AlienSpawn;
import polly.rx.core.orion.model.Portal;
import polly.rx.entities.AZEntry;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.DBHeatMapEntry;
import polly.rx.entities.DBPortal;
import polly.rx.entities.DBProduction;
import polly.rx.entities.DBQuadrant;
import polly.rx.entities.DBSector;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanHistoryEntry;
import polly.rx.entities.FleetScanShip;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.entities.TrainEntityV3;
import polly.rx.httpv2.AlienRaceModel;
import polly.rx.httpv2.AlienSpawnModel;
import polly.rx.httpv2.BattleReportModel;
import polly.rx.httpv2.BattleReportShipModel;
import polly.rx.httpv2.FleetScanShipTableModel;
import polly.rx.httpv2.FleetScanTableModel;
import polly.rx.httpv2.FleetScansWithShipModel;
import polly.rx.httpv2.OpenTrainingsModel;
import polly.rx.httpv2.PortalModel;
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
    public final static String DOCK_LEVEL = "DOCK_LEVEL"; //$NON-NLS-1$
    public static final String REPAIR_TIME_WARNING = "REPAIR_TIME_WARNING"; //$NON-NLS-1$


    private final FleetDBManager fleetDBManager;
    private final TrainManagerV2 trainManager;
    private final ScoreBoardManager sbeManager;
    private final AZEntryManager azManager;
    private ImageDatabase captchaDatabase;
    private RxCaptchaKiller captchaKiller;
    private final OrionChatProvider chatProvider;



    public MyPlugin(MyPolly myPolly)
                throws DuplicatedSignatureException, IncompatiblePluginException {
        super(myPolly);


        this.chatProvider = new DBOrionChatProvider(myPolly.persistence());
        addCommand(new IGMCommand(myPolly, this.chatProvider));

        /* capi train related */
        this.trainManager = new TrainManagerV2(myPolly);
        getMyPolly().persistence().registerEntity(TrainEntityV3.class);

        addCommand(new AddTrainCommand(myPolly, this.trainManager));
        addCommand(new CloseTrainCommand(myPolly, this.trainManager));
        addCommand(new MyTrainsCommand(myPolly, this.trainManager));
        addCommand(new DeliverTrainCommand(myPolly, this.trainManager));
        addCommand(new VenadCommand(myPolly));
        addCommand(new MyVenadCommand(myPolly));
        addCommand(new IPCommand(myPolly));
        addCommand(new CrackerCommand(myPolly));
        addCommand(new RessComand(myPolly));
        addCommand(new UrlAnonymizationCommand(myPolly));

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
        myPolly.persistence().registerEntity(DBHeatMapEntry.class);

        // orion
        myPolly.persistence().registerEntity(DBProduction.class);
        myPolly.persistence().registerEntity(DBSector.class);
        myPolly.persistence().registerEntity(DBQuadrant.class);
        myPolly.persistence().registerEntity(DBPortal.class);

        addCommand(new RankCommand(myPolly, this.sbeManager));


        myPolly.webInterface().addCategory(new MenuCategory(0, MSG.httpRxCategory));
        myPolly.webInterface().getServer().addController(
                new RXController(myPolly, this.fleetDBManager, this.sbeManager, this.azManager));


        final HTMLTableModel<FleetScan> scanModel = new FleetScanTableModel(this.fleetDBManager);
        final HTMLTable<FleetScan> fleetScanTable = new HTMLTable<FleetScan>("fleetScans", scanModel, myPolly); //$NON-NLS-1$

        final HTMLTableModel<FleetScanShip> scanShipModel = new FleetScanShipTableModel(this.fleetDBManager);
        final HTMLTable<FleetScanShip> fleetScanShipTable = new HTMLTable<FleetScanShip>("ships", scanShipModel, myPolly); //$NON-NLS-1$

        final HTMLTableModel<FleetScan> scansWithShip = new FleetScansWithShipModel(this.fleetDBManager);
        final HTMLTable<FleetScan> scansWithShipTable = new HTMLTable<FleetScan>("scansWithShip", scansWithShip, myPolly); //$NON-NLS-1$

        final HTMLTableModel<FleetScanShip> shipsForScanModel = new ShipsForScanTableModel(this.fleetDBManager);
        final HTMLTable<FleetScanShip> shipsForScanTable = new HTMLTable<FleetScanShip>("ships", shipsForScanModel, myPolly); //$NON-NLS-1$

        final HTMLTableModel<ScoreBoardEntry> scoreboard = new ScoreboardTableModel(this.sbeManager);
        final HTMLTable<ScoreBoardEntry> scoreboardTable = new HTMLTable<ScoreBoardEntry>("scoreboard", scoreboard, myPolly); //$NON-NLS-1$

        final HTMLTableModel<ScoreBoardEntry> scoreboardDetail = new ScoreboardDetailModel(this.sbeManager);
        final HTMLTable<ScoreBoardEntry> scoreboardDetailTable = new HTMLTable<>("entries", scoreboardDetail, myPolly); //$NON-NLS-1$

        final HTMLTableModel<BattleReport> reportModel = new BattleReportModel(this.fleetDBManager);
        final HTMLTable<BattleReport> reportTabble = new HTMLTable<BattleReport>("reports", reportModel, myPolly); //$NON-NLS-1$
        final StatisticsGatherer statsGatherer = new StatisticsGatherer();
        reportTabble.addModelListener(statsGatherer);


        final HTMLTableModel<TrainEntityV3> trainingModel = new TrainingTableModel(this.trainManager);
        final HTMLTable<TrainEntityV3> trainTable = new HTMLTable<>("closedTrainings", trainingModel, myPolly); //$NON-NLS-1$

        final HTMLTableModel<TrainEntityV3> openTrainingModel = new OpenTrainingsModel(this.trainManager);
        final HTMLTable<TrainEntityV3> openTrainTable = new HTMLTable<>("openTrainings", openTrainingModel, myPolly); //$NON-NLS-1$

        final HTMLTableModel<BattleReportShip> reportAttackerShipModel = new BattleReportShipModel(this.fleetDBManager, true);
        final HTMLTableModel<BattleReportShip> reportDefenderShipModel = new BattleReportShipModel(this.fleetDBManager, false);
        final HTMLTable<BattleReportShip> reportAttackerShipTable = new HTMLTable<>("attackerShips", reportAttackerShipModel, myPolly); //$NON-NLS-1$
        final HTMLTable<BattleReportShip> reportDefenderShipTable = new HTMLTable<>("defenderShips", reportDefenderShipModel, myPolly); //$NON-NLS-1$

        final HTMLTableModel<AlienRace> alienRaceModel = new AlienRaceModel();
        final HTMLTable<AlienRace> alienRaceTable = new HTMLTable<>("alienRaces", alienRaceModel, myPolly); //$NON-NLS-1$
        final HTMLTableModel<AlienSpawn> alienSpawnModel = new AlienSpawnModel();
        final HTMLTable<AlienSpawn> alienSpawnTable = new HTMLTable<>("alienSpawns", alienSpawnModel, myPolly); //$NON-NLS-1$

        final HTMLTableModel<Portal> portalModel = new PortalModel();
        final HTMLTable<Portal> portalTable = new HTMLTable<>("portals", portalModel, myPolly); //$NON-NLS-1$

        myPolly.webInterface().getServer().addHttpEventHandler("/api/allFleetScans", fleetScanTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allFleetScanShips", fleetScanShipTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scansWithShip", scansWithShipTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/shipsForScan", shipsForScanTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scoreboard", scoreboardTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/scoreboardDetail", scoreboardDetailTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allReports", reportTabble); //$NON-NLS-1$

        myPolly.webInterface().getServer().addHttpEventHandler("/api/reportAttackerShips", reportAttackerShipTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/reportDefenderShips", reportDefenderShipTable); //$NON-NLS-1$

        myPolly.webInterface().getServer().addHttpEventHandler("/api/alienRaces", alienRaceTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/alienSpawns", alienSpawnTable); //$NON-NLS-1$

        myPolly.webInterface().getServer().addHttpEventHandler("/api/portals", portalTable); //$NON-NLS-1$

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
        result.add(OrionController.MANAGE_RACE_PERMISSION);
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
        roleManager.assignPermission(ORION_ROLE, OrionController.MANAGE_RACE_PERMISSION);

        super.assignPermissions(roleManager);
    }



    @Override
    public void onLoad() throws PluginException {

        // Create captcha database#
        final File pluginFolder = getPluginFolder();
        final File captchas = new File(pluginFolder, "captchas"); //$NON-NLS-1$
        if (!captchas.exists()) {
            captchas.mkdirs();
        }
        this.captchaDatabase = new ImageDatabase(captchas.getPath(),
                new File(captchas, "db.txt").getPath()); //$NON-NLS-1$
        this.captchaKiller = new RxCaptchaKiller(this.captchaDatabase);

        // relearn database
        final File learning = new File(pluginFolder, "learning"); //$NON-NLS-1$
        if (learning.exists()) {
            this.captchaDatabase.learnFrom(learning.getPath());
        }


        // ORION
        final DBOrionAccess dboa = new DBOrionAccess(getMyPolly().persistence());
        final WormholeProvider holeProvider = new WLSWormholeProvider();
        final DBFleetHeatMap heatMap = new DBFleetHeatMap(getMyPolly().persistence());
        final FleetTracker tracker = new MemoryFleetTracker(heatMap);
        final ResourcePriceProvider priceProvider = new QZoneResourcePriceProvider();
        final VenadUserMapper userMapper = new VenadUserMapper(getMyPolly().users());
        final LoginCodeManager loginCodeManager = new LoginCodeManager(this.captchaKiller);
        Orion.initialize(
                dboa.getQuadrantProvider(),
                dboa.getQuadrantUpdater(),
                holeProvider,
                dboa.getPortalProvider(),
                dboa.getAlienManager(),
                dboa.getPortalUpdater(),
                tracker,
                priceProvider,
                userMapper,
                loginCodeManager,
                heatMap
            );

        final OrionChatController chatController = new OrionChatController(
                getMyPolly(), this.chatProvider);
        getMyPolly().webInterface().getServer().addController(chatController);

        final OrionController oc = new OrionController(getMyPolly(), this.azManager);
        final OrionNewsProvider newsProvider = new OrionNewsProvider(
                getMyPolly(),
                Orion.INSTANCE.getFleetTracker(),
                Orion.INSTANCE.getPortalUpdater(),
                this.trainManager);

        getMyPolly().webInterface().getServer().addController(oc);
        getMyPolly().webInterface().getServer().addHttpEventHandler(
                OrionNewsProvider.NEWS_URL, newsProvider);

        try {
            addCommand(new RouteCommand(getMyPolly()));
        } catch (DuplicatedSignatureException e1) {
            e1.printStackTrace();
        }


        try {
            getMyPolly().users().addAttribute(REPAIR_TIME_WARNING,
                    new Types.TimespanType(0), MSG.pluginRepairTimeWarning,
                    MSG.httpRxCategory, Constraints.TIMESPAN);
            getMyPolly().users().addAttribute(VENAD,
                new Types.StringType(VENAD_DEFAULT),
                MSG.pluginVenadDesc,
                MSG.httpRxCategory);
            getMyPolly().users().addAttribute(CRACKER, new Types.NumberType(0.0),
                MSG.pluginCrackerDesc,
                MSG.httpRxCategory, Constraints.INTEGER);
            getMyPolly().users().addAttribute(AUTO_REMIND,
                    new Types.BooleanType(false),
                    MSG.pluginAutoRemindDesc,
                    MSG.httpRxCategory);
            getMyPolly().users().addAttribute(AUTO_REMIND_AZ,
                    new Types.TimespanType(840), MSG.pluginAutoRemindAzDesc,
                    MSG.httpRxCategory, Constraints.TIMESPAN);
            getMyPolly().users().addAttribute(LOW_PZ_WARNING,
                    new Types.NumberType(0.0),
                    MSG.pluginLowPzWarningDesc,
                    MSG.httpRxCategory, Constraints.INTEGER);

            getMyPolly().users().addAttribute(PORTALS, new Types.StringType(""),  //$NON-NLS-1$
                    MSG.pluginPortalDesc, MSG.httpRxCategory,
                    new SectorListAttributeConstraint());

            getMyPolly().users().addAttribute(DOCK_LEVEL,
                    new Types.NumberType(1),
                    MSG.pluginDockLvlDescription, MSG.httpRxCategory,
                    new DockLevelConstraint());


        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        try {
            this.fleetDBManager.cleanInvalidBattleReports();
            this.fleetDBManager.cleanInvalidFleetScans();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}
