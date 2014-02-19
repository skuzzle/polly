package polly.rx.httpv2;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.core.AZEntryManager;
import polly.rx.core.FleetDBManager;
import polly.rx.core.ScoreBoardManager;
import polly.rx.core.ScoreBoardManager.EntryResult;
import polly.rx.core.orion.http.OrionController;
import polly.rx.entities.AZEntry;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.graphs.NamedPoint;
import polly.rx.httpv2.StatisticsGatherer.BattleReportStatistics;
import polly.rx.parsing.BattleReportParser;
import polly.rx.parsing.FleetScanParser;
import polly.rx.parsing.ParseException;
import polly.rx.parsing.QBattleReportParser;
import polly.rx.parsing.QFleetScanParser;
import polly.rx.parsing.ScoreBoardParser;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.answers.HttpInputStreamAnswer;
import de.skuzzle.polly.http.api.answers.HttpResourceAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.BooleanType;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.io.FastByteArrayOutputStream;


public class RXController extends PollyController {
    
    public final static String PAGE_FLEET_SCAN_MANAGER = "/pages/fleetScanPage"; //$NON-NLS-1$
    public final static String PAGE_FLEET_SCAN_DETAILS = "/pages/fleetScanDetails"; //$NON-NLS-1$
    public final static String PAGE_FLEET_SCAN_SHIPS = "/pages/fleetScanShips"; //$NON-NLS-1$
    public final static String PAGE_SCAN_SHIP_DETAILS = "/pages/scanShipDetails"; //$NON-NLS-1$
    public final static String PAGE_SCOREBOARD = "/pages/scoreboard"; //$NON-NLS-1$
    public final static String PAGE_REPORTS = "/pages/reports"; //$NON-NLS-1$
    public final static String PAGE_CONFIGURE_AZ = "/pages/configureAz"; //$NON-NLS-1$
    public final static String PAGE_GM_SCRIPTS = "/pages/gmScripts"; //$NON-NLS-1$
    public final static String PAGE_COMPARE_SCORES = "/pages/score/compare"; //$NON-NLS-1$
    public final static String PAGE_SCORE_DETAILS = "/pages/score/details"; //$NON-NLS-1$
    public final static String PAGE_REPORT_DETAILS = "/pages/reportDetails"; //$NON-NLS-1$
    public final static String PAGE_TRAININGS = "/pages/trainings"; //$NON-NLS-1$
    
    
    public final static String API_ADD_TO_COMPARE = "/api/addToCompare"; //$NON-NLS-1$
    public final static String API_REMOVE_FROM_COMPARE = "/api/removeFromCompare"; //$NON-NLS-1$
    public final static String API_ADD_AZ_ENTRY = "/api/addAzEntry"; //$NON-NLS-1$
    public final static String API_DELETE_AZ_ENTRY = "/api/deleteAzEntry"; //$NON-NLS-1$
    public final static String API_POST_FLEET_SCAN = "/api/postFleetScan"; //$NON-NLS-1$
    public final static String API_POST_QFLEET_SCAN = "/api/postQFleetScan"; //$NON-NLS-1$
    public final static String API_GRAPH_COMPARE = "/api/graphCompare"; //$NON-NLS-1$
    public final static String API_GRAPH_FOR_VENAD = "/api/graphForVenad"; //$NON-NLS-1$
    public final static String API_IMAGE_FROM_SESSION = "/api/imageFromSession"; //$NON-NLS-1$
    public final static String API_POST_SCOREBOARD = "/api/postScoreboard"; //$NON-NLS-1$
    public final static String API_POST_REPORT = "/api/postReport"; //$NON-NLS-1$
    public final static String API_POST_QREPORT = "/postQReport"; //$NON-NLS-1$
    public final static String API_DELETE_REPORT = "/api/deleteReport"; //$NON-NLS-1$
    public final static String API_REPORT_STATISTICS = "/api/battlereportStatistics"; //$NON-NLS-1$

    public final static String GM_SCRAPE_SCOREBOARD = "/GM/scrapescoreboarddata.user.js"; //$NON-NLS-1$
    public final static String GM_KB_REPORT = "/GM/kbreport.user.js"; //$NON-NLS-1$
    public final static String GM_FLEET_SCANS = "/GM/fleetscan.user.js"; //$NON-NLS-1$
    public final static String GM_ORION = "/GM/orion.user.js"; //$NON-NLS-1$
    public final static String GM_ORIONV2 = "/GM/orionv2.user.js"; //$NON-NLS-1$
    public final static String GM_ORIONV2_META = "/GM/orionv2.meta.js"; //$NON-NLS-1$
    
    public final static String FILES_VIEW = "/polly/rx/httpv2/view"; //$NON-NLS-1$
    
    
    private final static String CONTENT_FLEET_SCAN_MANAGER = "polly/rx/httpv2/view/fleetscans.overview.html"; //$NON-NLS-1$
    private final static String CONTENT_FLEET_SCAN_DETAILS = "polly/rx/httpv2/view/fleetscan.details.html"; //$NON-NLS-1$
    private final static String CONTENT_FLEET_SCAN_SHIPS = "polly/rx/httpv2/view/scanships.overview.html"; //$NON-NLS-1$
    private final static String CONTENT_SCAN_SHIP_DETAILS = "polly/rx/httpv2/view/scanship.details.html"; //$NON-NLS-1$
    private final static String CONTENT_SCOREBOARD = "polly/rx/httpv2/view/scoreboard.overview.html"; //$NON-NLS-1$
    private final static String CONTENT_REPORTS = "polly/rx/httpv2/view/battlereports.overview.html"; //$NON-NLS-1$
    private final static String CONTENT_CONFIG_AZ = "polly/rx/httpv2/view/configure.az.html"; //$NON-NLS-1$
    private final static String CONTENT_GM_SCRIPTS = "polly/rx/httpv2/view/gmscripts.html"; //$NON-NLS-1$
    private final static String CONTENT_COMPARE_SCORES = "polly/rx/httpv2/view/scoreboard.compare.html"; //$NON-NLS-1$
    private final static String CONTENT_SCORE_DETAILS = "polly/rx/httpv2/view/scoreboard.details.html"; //$NON-NLS-1$
    private final static String CONTENT_GRAPH_COMPARE = "polly/rx/httpv2/view/graph.html"; //$NON-NLS-1$
    private final static String CONTENT_GRAPH_FOR_VENAD = "polly/rx/httpv2/view/graph.html"; //$NON-NLS-1$
    private final static String CONTENT_REPORT_DETAILS = "polly/rx/httpv2/view/battlereports.details.html"; //$NON-NLS-1$
    private final static String CONTENT_REPORT_STATISTICS = "polly/rx/httpv2/view/battlereports.statistics.html"; //$NON-NLS-1$
    private final static String CONTENT_SCRAPE_SCOREBOARD = "polly/rx/httpv2/view/scrapescoreboarddata.user.js"; //$NON-NLS-1$
    private final static String CONTENT_KB_REPORT = "polly/rx/httpv2/view/kbreport.user.js"; //$NON-NLS-1$
    private final static String CONTENT_FLEET_SCANS = "polly/rx/httpv2/view/fleetscan.user.js"; //$NON-NLS-1$
    private final static String CONTENT_TRAININGS = "polly/rx/httpv2/view/trainings.html"; //$NON-NLS-1$
    private final static String CONTENT_GM_ORION = "polly/rx/httpv2/view/orion.user.js"; //$NON-NLS-1$
    private final static String CONTENT_GM_ORIONV2 = "polly/rx/httpv2/view/orionv2.user.js"; //$NON-NLS-1$
    private final static String CONTENT_GM_ORIONV2_META = "polly/rx/httpv2/view/orionv2.meta.js"; //$NON-NLS-1$
    
    private final static String REVORIX_CATEGORY_KEY = "httpRxCategory"; //$NON-NLS-1$
    private final static String FLEET_SCAN_NAME_KEY = "httpFleetScanMngr"; //$NON-NLS-1$
    private final static String FLEET_SCAN_DESC_KEY = "httpFleetScanMngrDesc"; //$NON-NLS-1$
    private final static String SCANNED_SHIPS_NAME_KEY = "httpScannedShips"; //$NON-NLS-1$
    private final static String SCANNED_SHIPS_DESC_KEY = "httpScannedShipsDesc"; //$NON-NLS-1$
    private final static String SCOREBOARD_NAME_KEY = "httpScoreboardMngr"; //$NON-NLS-1$
    private final static String SCOREBOARD_DESC_KEY = "httpScoreboardMngrDesc"; //$NON-NLS-1$
    private final static String REPORTS_NAME_KEY = "httpReportsMngr"; //$NON-NLS-1$
    private final static String REPORTS_DESC_KEY = "httpReportsMngrDesc"; //$NON-NLS-1$
    private final static String CONFIG_AZ_NAME_KEY = "httpAzMngr"; //$NON-NLS-1$
    private final static String CONFIG_AZ_DESC_KEY = "httpAzMngrDesc"; //$NON-NLS-1$
    private final static String GM_SCRIPT_NAME_KEY = "httpGmScripts"; //$NON-NLS-1$
    private final static String GM_SCRIPT_DESC_KEY = "httpGmScriptsDesc"; //$NON-NLS-1$
    private final static String TRAININGS_NAME_KEY = "htmlTrainingCaption"; //$NON-NLS-1$
    private final static String TRAININGS_DESC_KEY = "htmlTrainingDescription"; //$NON-NLS-1$
    
    
    private final static String COMPARE_LIST_KEY = "COMPARE_LIST"; //$NON-NLS-1$
    private final static String MULTI_GRAPH_PREFIX = "graph_compare_mm_"; //$NON-NLS-1$
    private final static String SINGLE_GRAPH_PREFIX = "graph_"; //$NON-NLS-1$
    public final static String STATS_PREFIX = "BR_STATS_"; //$NON-NLS-1$
    
    private final FleetDBManager fleetDb;
    private final ScoreBoardManager sbManager;
    private final AZEntryManager azManager;
    
    
    
    public RXController(MyPolly myPolly, FleetDBManager fleetDb, 
                ScoreBoardManager sbManager, AZEntryManager azManager) {
        super(myPolly);
        this.azManager = azManager;
        this.fleetDb = fleetDb;
        this.sbManager = sbManager;
    }
    
    

    @Override
    protected Controller createInstance() {
        return new RXController(this.getMyPolly(), this.fleetDb, this.sbManager, 
                this.azManager);
    }
    
    
    
    @Override
    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = super.createContext(content);
        HTMLTools.gainFieldAccess(c, MSG.class, "MSG"); //$NON-NLS-1$
        return c;
    }
    
    
    
    @Get(value = PAGE_TRAININGS, name = TRAININGS_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        TRAININGS_DESC_KEY,
        RoleManager.REGISTERED_PERMISSION })
    public HttpAnswer trainingsPage() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.REGISTERED_PERMISSION);
        return this.makeAnswer(this.createContext(CONTENT_TRAININGS));
    }
    
    
    
    @Get(value = PAGE_FLEET_SCAN_MANAGER, name = FLEET_SCAN_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        FLEET_SCAN_DESC_KEY,
        FleetDBManager.VIEW_FLEET_SCAN_PERMISSION })
    public HttpAnswer fleetScanPage() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        return this.makeAnswer(this.createContext(CONTENT_FLEET_SCAN_MANAGER));
    }
    
    
    
    @Get(PAGE_FLEET_SCAN_DETAILS)
    public HttpAnswer fleetScanDetailsPage(@Param("scanId") int scanId) 
                throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        final FleetScan scan = this.fleetDb.getScanById(scanId);
        final Map<String, Object> c = this.createContext(
                CONTENT_FLEET_SCAN_DETAILS);
        c.put("scan", scan); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = PAGE_FLEET_SCAN_SHIPS, name = SCANNED_SHIPS_NAME_KEY)
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        SCANNED_SHIPS_DESC_KEY,
        FleetDBManager.VIEW_FLEET_SCAN_PERMISSION })
    public HttpAnswer fleetScanShipsPage() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        return this.makeAnswer(this.createContext(CONTENT_FLEET_SCAN_SHIPS));
    }
    
    
    
    @Get(PAGE_SCAN_SHIP_DETAILS)
    public HttpAnswer scanShipDetails(@Param("shipId") int id) 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        final FleetScanShip ship = this.fleetDb.getShipByRevorixId(id);
        final Map<String, Object> c = this.createContext(CONTENT_SCAN_SHIP_DETAILS);
        c.put("ship", ship); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    
    public final class CompareList extends SuccessResult {
        private Set<String> entries;
        
        
        public CompareList() {
            super(true, ""); //$NON-NLS-1$
            this.entries = new HashSet<>();
        }
        
        
        
        public Set<String> getEntries() {
            return this.entries;
        }
        
        
        
        @Override
        public String toString() {
            return this.entries.toString();
        }
    }
    
    
    
    @Get(API_ADD_TO_COMPARE)
    public HttpAnswer addToCompare(@Param("venadName") String name) 
                throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        CompareList cl = (CompareList) this.getSession().get(COMPARE_LIST_KEY);
        if (cl == null) {
            cl = new CompareList();
            this.getSession().set(COMPARE_LIST_KEY, cl);
        }
        synchronized (cl) {
            cl.getEntries().add(name);
        }
        return HttpAnswers.newRedirectAnswer(PAGE_SCOREBOARD);
    }
    
    
    
    @Get(API_REMOVE_FROM_COMPARE)
    public HttpAnswer removeFromCompare(@Param("venadName") String name) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        CompareList cl = (CompareList) this.getSession().get(COMPARE_LIST_KEY);
        if (cl != null) {
            synchronized (cl) {
                cl.getEntries().remove(name);
            }
        }
        return HttpAnswers.newRedirectAnswer(PAGE_SCOREBOARD);
    }
    
    
    
    @Get(value = PAGE_SCOREBOARD, name = SCOREBOARD_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        SCOREBOARD_DESC_KEY,
        MyPlugin.SBE_PERMISSION })
    public HttpAnswer scoreboardPage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_SCOREBOARD);
        CompareList cl = (CompareList) this.getSession().get(COMPARE_LIST_KEY);
        if (cl == null) {
            cl = new CompareList();
            this.getSession().set(COMPARE_LIST_KEY, cl);
        }
        c.put("compareList", cl); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = PAGE_REPORTS, name = REPORTS_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY,
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        REPORTS_DESC_KEY,
        FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION })
    public HttpAnswer reportsPage() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_REPORTS);
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = PAGE_CONFIGURE_AZ, name = CONFIG_AZ_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        CONFIG_AZ_DESC_KEY,
        FleetDBManager.ADD_BATTLE_REPORT_PERMISSION })
    public HttpAnswer configureAz() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        final User user = this.getSessionUser();
        final List<AZEntry> entries = this.azManager.getEntries(user.getId());
        
        final Map<String, Object> c = this.createContext(CONTENT_CONFIG_AZ);
        c.put("entries", entries); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_ADD_AZ_ENTRY)
    public HttpAnswer addAzEntry(@Param("fleet") String fleet, 
            @Param("az") String az,
            @Param("jumpTime") String jumpTime) 
            throws AlternativeAnswerException, DatabaseException {
        
        this.requirePermissions(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        final User user = this.getSessionUser();
        final Types t = this.getMyPolly().parse(az);
        
        if (!(t instanceof TimespanType)) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpAddAzIllegalFormat));
        }
        
        this.azManager.addEntry(user.getId(), fleet, az, jumpTime);
        return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
    }
    
    
    
    @Get(API_DELETE_AZ_ENTRY)
    public HttpAnswer deleteAzEntry(@Param("entryId") int id) 
            throws AlternativeAnswerException, DatabaseException {
        this.requirePermissions(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        final User user = this.getSessionUser();
        
        try {
            this.azManager.deleteEntry(id, user.getId());
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    @Get(value = PAGE_GM_SCRIPTS, name = GM_SCRIPT_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY,
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY,
        GM_SCRIPT_DESC_KEY,
        RoleManager.REGISTERED_PERMISSION })
    public HttpAnswer gmScripts() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.REGISTERED_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_GM_SCRIPTS);
        return this.makeAnswer(c);
    }
    
    
    
    @Get(PAGE_COMPARE_SCORES)
    public HttpAnswer compare() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_COMPARE_SCORES);
        CompareList cl = (CompareList) this.getSession().get(COMPARE_LIST_KEY);
        c.put("compareList", cl); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(PAGE_SCORE_DETAILS)
    public HttpAnswer venadDetails(@Param("venadName") String name) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_SCORE_DETAILS);
        c.put("venad", name); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Post(API_POST_FLEET_SCAN)
    public HttpAnswer postFleetScan(
            @Param("scan") String scan,
            @Param("quadrant") String quadrant,
            @Param("x") int x,
            @Param("y") int y,
            @Param(value = "meta", optional = true) String meta) 
                    throws AlternativeAnswerException {
        
        this.requirePermissions(FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        
        try {
            final FleetScan fs = FleetScanParser.parseFleetScan(
                    scan, quadrant, x, y, meta);
            this.fleetDb.addFleetScan(fs);
        } catch (ParseException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        return new GsonHttpAnswer(200, new SuccessResult(true, MSG.httpPostScanSuccess));
    }
    
    
    
    @Post(API_POST_QFLEET_SCAN)
    public HttpAnswer postQFleetScan(@Param("scan") String scan, 
            @Param("user") String user, @Param("pw") String pw) 
            throws AlternativeAnswerException {
        
        final User u = this.checkLogin(user, pw);
        if (!this.getMyPolly().roles().hasPermission(u, 
                    FleetDBManager.ADD_FLEET_SCAN_PERMISSION)) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpNoPermission));
        }
        
        try {
            final FleetScan fs = QFleetScanParser.parseFleetScan(scan);
            this.fleetDb.addFleetScan(fs);
        } catch (ParseException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        return new GsonHttpAnswer(200, new SuccessResult(true, MSG.httpPostScanSuccess));
    }
    
    
            
    @Get(API_GRAPH_COMPARE)
    public HttpAnswer graphCompare(@Param("maxMonth") int maxMonths) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        CompareList cl = (CompareList) this.getSession().get(COMPARE_LIST_KEY);
        if (cl == null) {
            return HttpAnswers.newStringAnswer(""); //$NON-NLS-1$
        }
        final Collection<NamedPoint> allPoints = new ArrayList<>();
        final OutputStream graph = this.sbManager.createMultiGraph(maxMonths, allPoints, 
                cl.getEntries().toArray(new String[0]));
        
        final String imgName = MULTI_GRAPH_PREFIX + maxMonths; 
        this.getSession().set(imgName, graph);
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        c.put("allPoints", allPoints); //$NON-NLS-1$
        c.put("imgName", imgName); //$NON-NLS-1$
        c.put("maxMonths", maxMonths); //$NON-NLS-1$
        c.put("isCompare", true); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_GRAPH_COMPARE, c);
    }
    
    
    
    @Get(API_GRAPH_FOR_VENAD)
    public HttpAnswer graphForVenad(@Param("venadName") String name, 
            @Param("maxMonth") int maxMonths) throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        
        final List<ScoreBoardEntry> entries = this.sbManager.getEntries(name);
        Collections.sort(entries, ScoreBoardEntry.BY_DATE);
        
        final Collection<NamedPoint> allPoints = new ArrayList<>();
        final OutputStream graph = this.sbManager.createLatestGraph(entries, maxMonths, 
                allPoints);
        
        final String imgName = SINGLE_GRAPH_PREFIX + name + maxMonths; 
        this.getSession().set(imgName, graph);
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        c.put("allPoints", allPoints); //$NON-NLS-1$
        c.put("venad", name); //$NON-NLS-1$
        c.put("imgName", imgName); //$NON-NLS-1$
        c.put("maxMonths", maxMonths); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_GRAPH_FOR_VENAD, c);
    }
    
    
    
    @Get(API_IMAGE_FROM_SESSION)
    public HttpAnswer imageFromSession(@Param("imgName") String imgName) 
            throws AlternativeAnswerException, IOException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final FastByteArrayOutputStream out = this.getSession().get(imgName);
        return new HttpInputStreamAnswer(200, out.getInputStreamForBuffer());
    }
    
    
    
    @Get(FILES_VIEW)
    public HttpAnswer getFile() {
        final ClassLoader cl = this.getClass().getClassLoader();
        return new HttpResourceAnswer(200, cl, this.getEvent().getPlainUri());
    }
    
    
    
    @Post(API_POST_SCOREBOARD)
    public HttpAnswer postScoreboard(@Param("input") String input) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        
        try {
            final Collection<ScoreBoardEntry> entries = 
                    ScoreBoardParser.parse(input, Time.currentTime());
            this.sbManager.addEntries(entries);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HttpAnswers.newRedirectAnswer(PAGE_SCOREBOARD);
    }
    
    
    
    public final class PostScoreBoardResult extends SuccessResult {
        
        public final EntryResult[] entries;
        
        public PostScoreBoardResult(String msg, EntryResult[] results) {
            super(true, msg);
            this.entries = results;
        }
    }
    
    
    @Post(API_POST_SCOREBOARD)
    public HttpAnswer postScoreboardExt(
            @Param("user") String user, 
            @Param("pw") String pw,
            @Param("paste") String paste) throws AlternativeAnswerException {
        
        final User u = this.checkLogin(user, pw);
        if (!this.getMyPolly().roles().hasPermission(u, MyPlugin.SBE_PERMISSION)) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpNoPermission));
        }
        
        try {
            final Collection<ScoreBoardEntry> entries =
                    ScoreBoardParser.parse(paste, Time.currentTime());
            final List<EntryResult> results = this.sbManager.addEntries(entries);
            final EntryResult[] resultsArr = new EntryResult[results.size()];
            results.toArray(resultsArr);
            
            return new GsonHttpAnswer(200, 
                    new PostScoreBoardResult( 
                            MSG.bind(MSG.httpPostScoreboardSuccess, entries.size()), 
                            resultsArr));
        } catch (ParseException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        
    }
    
    
    
    @Get(PAGE_REPORT_DETAILS)
    public HttpAnswer reportDetails(@Param("reportId") int reportId) 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        
        final BattleReport br = this.fleetDb.getReportById(reportId);
        final Map<String, Object> c = this.createContext(CONTENT_REPORT_DETAILS);
        
        c.put("report", br); //$NON-NLS-1$
        c.put("df", new DecimalFormat("0.##"));  //$NON-NLS-1$//$NON-NLS-2$
        c.put("fleetDb", this.fleetDb); //$NON-NLS-1$
        c.put("Math", Math.class); //$NON-NLS-1$
        this.prepareContext(br.getAttackerShips(), "Attacker", c); //$NON-NLS-1$
        this.prepareContext(br.getDefenderShips(), "Defender", c); //$NON-NLS-1$
        
        return this.makeAnswer(c);
    }
    
    
    
    private void prepareContext(List<BattleReportShip> ships, 
            String postfix, Map<String, Object> c) {
    
        int pzDamage = 0;
        int maxPzDamage = 0;
        int minPzDamage = Integer.MAX_VALUE;
        int avgPzDamage = 0;
        
        int shieldDamage = 0;
        int maxShieldDamage = 0;
        int minShieldDamage = Integer.MAX_VALUE;
        int avgShieldDamage = 0;
        
        int capiXp = 0;
        int maxCapiXp = 0;
        int minCapiXp = Integer.MAX_VALUE;
        int avgCapiXp = 0;
        
        int crewXp = 0;
        int maxCrewXp = 0;
        int minCrewXp = Integer.MAX_VALUE;
        int avgCrewXp = 0;
        
        int maxWend = 0;
        int minWend = Integer.MAX_VALUE;
        
        for (BattleReportShip ship : ships) {
            pzDamage += ship.getPzDamage();
            maxPzDamage = Math.max(maxPzDamage, ship.getPzDamage());
            minPzDamage = Math.min(minPzDamage, ship.getPzDamage());
            
            shieldDamage += ship.getShieldDamage();
            maxShieldDamage = Math.max(maxShieldDamage, ship.getShieldDamage());
            minShieldDamage = Math.min(minShieldDamage, ship.getShieldDamage());
            
            capiXp += ship.getCapiXp();
            maxCapiXp = Math.max(maxCapiXp, ship.getCapiXp());
            minCapiXp = Math.min(minCapiXp, ship.getCapiXp());
            
            crewXp += ship.getCrewXp();
            maxCrewXp = Math.max(maxCrewXp, ship.getCrewXp());
            minCrewXp = Math.min(minCrewXp, ship.getCrewXp());
            
            maxWend = Math.max(maxWend, ship.getMaxWend());
            minWend = Math.min(minWend, ship.getMaxWend());
        }
        
        avgPzDamage = pzDamage / ships.size();
        avgShieldDamage = shieldDamage / ships.size();
        avgCapiXp = capiXp / ships.size();
        avgCrewXp = crewXp / ships.size();
        
        c.put("pzDamage" + postfix, pzDamage); //$NON-NLS-1$
        c.put("maxPzDamage" + postfix, maxPzDamage); //$NON-NLS-1$
        c.put("minPzDamage" + postfix, minPzDamage); //$NON-NLS-1$
        c.put("avgPzDamage" + postfix, avgPzDamage); //$NON-NLS-1$
        c.put("shieldDamage" + postfix, shieldDamage); //$NON-NLS-1$
        c.put("maxShieldDamage" + postfix, maxShieldDamage); //$NON-NLS-1$
        c.put("minShieldDamage" + postfix, minShieldDamage); //$NON-NLS-1$
        c.put("avgShieldDamage" + postfix, avgShieldDamage); //$NON-NLS-1$
        c.put("capiXp" + postfix, capiXp); //$NON-NLS-1$
        c.put("maxCapiXp" + postfix, maxCapiXp); //$NON-NLS-1$
        c.put("minCapiXp" + postfix, minCapiXp); //$NON-NLS-1$
        c.put("avgCapiXp" + postfix, avgCapiXp); //$NON-NLS-1$
        c.put("crewXp" + postfix, crewXp); //$NON-NLS-1$
        c.put("maxCrewXp" + postfix, maxCrewXp); //$NON-NLS-1$
        c.put("minCrewXp" + postfix, minCrewXp); //$NON-NLS-1$
        c.put("avgCrewXp" + postfix, avgCrewXp); //$NON-NLS-1$
        c.put("maxWend" + postfix, maxWend); //$NON-NLS-1$
        c.put("minWend" + postfix, minWend); //$NON-NLS-1$
    }
    
    
    
    @Post(API_POST_REPORT)
    public HttpAnswer postReport(@Param("report") String report) 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        try {
            final BattleReport br = BattleReportParser.parseReport(report, 
                    this.getSessionUser());
            
            this.fleetDb.addBattleReport(br);
        } catch (ParseException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        return new GsonHttpAnswer(200, 
                new SuccessResult(true, MSG.httpPostReportSuccess));
    }
    
    
    
    public static class QReportResult extends SuccessResult {

        public final String lowPzShips;
        public final boolean lowPzWarning = true;
        
        public QReportResult(boolean success, String message, String lowPzShips) {
            super(success, message);
            this.lowPzShips = lowPzShips;
        }
    }
    
    
    
    @Post(API_POST_QREPORT)
    public HttpAnswer postQReport(
            @Param("user") String user, 
            @Param("pw") String pw, 
            @Param(value = "isLive", optional = true, defaultValue = "false") Boolean isLive,
            @Param("report") String report) {
        
        // TODO: replace with this.checkLogin when battle reports are integrated in orion
        final User u = this.getMyPolly().users().getUser(user);
        if (u == null || !u.checkPassword(pw)) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpIllegalLogin));
        } else if (!this.getMyPolly().roles().hasPermission(u, 
                    FleetDBManager.ADD_BATTLE_REPORT_PERMISSION)) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpNoPermission));
        }
        
        final BattleReport br;
        try {
            br = QBattleReportParser.parse(report, u.getId());
        } catch (ParseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        
        final NumberType pzWarning = (NumberType) 
                u.getAttribute(MyPlugin.LOW_PZ_WARNING);
        
        List<BattleReportShip> lowPzShips = new ArrayList<>(40);
        if (pzWarning.getValue() > 0.0) {
            final List<BattleReportShip> ships = br.getTactic() == BattleTactic.ALIEN 
                    ? br.getDefenderShips()
                    : br.getAttackerShips();
                    
            for (final BattleReportShip ship : ships) {
                if (ship.getCurrentPz() < pzWarning.getValue()) {
                    lowPzShips.add(ship);
                }
            }
        }
        
        if (isLive && u.getCurrentNickName() != null) {
            final BooleanType autoRemind = (BooleanType) u.getAttribute(MyPlugin.AUTO_REMIND);
            if (autoRemind.getValue()) {
                
                // get AZ for attacker fleet. This will fall back to the default az
                // time if no time for given fleet is configured
                final TimespanType az = this.azManager.getAz(
                        br.getAttackerFleetName(), u);
                
                final String duration = az.getSpan() + "s"; //$NON-NLS-1$
                final String command = MSG.bind(MSG.httpAutoRemindCommand, 
                        u.getCurrentNickName(), duration, br.getAttackerFleetName());;
                
                try {
                    this.getMyPolly().commands().executeString(command, 
                            u.getCurrentNickName(), true, u, this.getMyPolly().irc());
                } catch (Exception e) {
                    // ignore
                    e.printStackTrace();
                }
            }
        }
        
        
        String message = MSG.httpPostReportSuccess;
        try {
            this.fleetDb.addBattleReport(br);
        } catch (DatabaseException e) {
            message = e.getMessage();
        }
        
        
        if (!lowPzShips.isEmpty()) {
            final StringBuilder b = new StringBuilder();
            b.append(MSG.bind(MSG.httpShipsBelow, 
                    pzWarning.valueString(this.getMyPolly().formatting())));
            b.append("\n"); //$NON-NLS-1$
            for (final BattleReportShip lowPz : lowPzShips) {
                b.append(lowPz.getName());
                b.append(" ("); //$NON-NLS-1$
                b.append(lowPz.getCurrentPz());
                b.append("pz)\n"); //$NON-NLS-1$
            }
            return new GsonHttpAnswer(200, 
                    new QReportResult(true, message, b.toString()));
        }
        return new GsonHttpAnswer(200, new SuccessResult(true, message));            
    }
    
    
    
    @Get(API_DELETE_REPORT)
    public HttpAnswer deleteReport(@Param("reportId") int id) 
            throws DatabaseException, AlternativeAnswerException {
        
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                FleetDBManager.DELETE_BATTLE_REPORT_PERMISSION)) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpNoPermission));
        }
        this.fleetDb.deleteReportById(id);
        return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
    }
    

    
    @Get(API_REPORT_STATISTICS)
    public HttpAnswer battleReportStatistics() {
        
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION)) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpNoPermission));
        }
        
        final User user = this.getSessionUser();
        final String STATISTIC_KEY = STATS_PREFIX + user.getName();
        BattleReportStatistics stats = 
                (BattleReportStatistics) this.getSession().get(STATISTIC_KEY);
        
        if (stats == null) {
            stats = new BattleReportStatistics();
        }
        
        synchronized (stats) {
            final DecimalFormat df = new DecimalFormat("#.##"); //$NON-NLS-1$
            final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
            c.put("df", df); //$NON-NLS-1$
            c.put("capiXpSumAttacker", stats.capiXpSumAttacker); //$NON-NLS-1$
            c.put("crewXpSumAttacker", stats.crewXpSumAttacker); //$NON-NLS-1$
            c.put("capiXpSumDefender", stats.capiXpSumDefender); //$NON-NLS-1$
            c.put("crewXpSumDefender", stats.crewXpSumDefender); //$NON-NLS-1$
            c.put("pzDamageAttacker", stats.pzDamageAttacker); //$NON-NLS-1$
            c.put("pzDamageDefender", stats.pzDamageDefender); //$NON-NLS-1$
            c.put("repairTimeAttacker", stats.repairTimeAttacker); //$NON-NLS-1$
            c.put("repairTimeDefender", stats.repairTimeDefender); //$NON-NLS-1$
            c.put("repairCostDefender", stats.repairCostDefender); //$NON-NLS-1$
            c.put("repairCostAttacker", stats.repairCostAttacker); //$NON-NLS-1$
            c.put("kwAttacker", stats.kwAttacker); //$NON-NLS-1$
            c.put("kwDefender", stats.kwDefender); //$NON-NLS-1$
            
            c.put("artifacts", stats.artifacts); //$NON-NLS-1$
            c.put("chance", stats.artifactChance * 100.0); //$NON-NLS-1$
            
            c.put("dropSum", stats.dropSum); //$NON-NLS-1$
            c.put("dropMax", stats.dropMax); //$NON-NLS-1$
            c.put("dropMin", stats.dropMin); //$NON-NLS-1$
            c.put("dropNetto", stats.dropNetto); //$NON-NLS-1$
            c.put("dropPrices", stats.dropPrices); //$NON-NLS-1$
            c.put("currentPrices", stats.currentPrices);  //$NON-NLS-1$
            c.put("dropPriceSum", stats.dropPriceSum); //$NON-NLS-1$
            c.put("dropPriceSumAtDropTime", stats.dropPriceSumAtDropTime); //$NON-NLS-1$
            c.put("dropPricesAtDropTime", stats.dropPricesAtDropTime); //$NON-NLS-1$
            c.put("reportSize", stats.reportSize); //$NON-NLS-1$
            return HttpAnswers.newTemplateAnswer(CONTENT_REPORT_STATISTICS, c);
        }
    }
    
    
    
    @Get(GM_SCRAPE_SCOREBOARD)
    public HttpAnswer installScoreboard() 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final String host = this.createPollyUrl();
        
        final Map<String, String> c = new HashMap<String, String>();
        c.put("host", host); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_SCRAPE_SCOREBOARD, c);
    }
    
    
    
    @Get(GM_KB_REPORT)
    public HttpAnswer installLiveKB() 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        final String host = this.createPollyUrl();
        
        final Map<String, String> c = new HashMap<String, String>();
        c.put("api", API_POST_QREPORT); //$NON-NLS-1$
        c.put("host", host); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_KB_REPORT, c);
    }
    
    
    
    @Get(GM_FLEET_SCANS)
    public HttpAnswer installFleetScans() 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        final String host = this.createPollyUrl();
        
        final Map<String, String> c = new HashMap<String, String>();
        c.put("api", API_POST_QFLEET_SCAN); //$NON-NLS-1$
        c.put("host", host); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_FLEET_SCANS, c);
    }
    
    
    
    
    @Get(GM_ORION)
    public HttpAnswer installOrion() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        final String host = this.createPollyUrl();
        
        final Map<String, String> c = new HashMap<String, String>();
        c.put("sectorApi", OrionController.API_JSON_SECTOR); //$NON-NLS-1$
        c.put("quadrantApi", OrionController.API_JSON_QUADRANT); //$NON-NLS-1$
        c.put("postSectorApi", OrionController.API_JSON_POST_SECTOR); //$NON-NLS-1$
        c.put("host", host); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_GM_ORION, c);
    }
    
    
    
    
    @Get(GM_ORIONV2_META)
    public HttpAnswer orionV2MetaData() {
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        final String host = this.createPollyUrl();
        c.put("host", host); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_GM_ORIONV2_META, c);
    }
    
    
    
    @Get(GM_ORIONV2)
    public HttpAnswer installOrionV2() throws AlternativeAnswerException {
        final String host = this.createPollyUrl();
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        c.put("sectorApi", OrionController.API_JSON_SECTOR); //$NON-NLS-1$
        c.put("quadrantApi", OrionController.API_JSON_QUADRANT); //$NON-NLS-1$
        c.put("postSectorApi", OrionController.API_JSON_POST_SECTOR); //$NON-NLS-1$
        c.put("host", host); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_GM_ORIONV2, c);
    }
    
    
    
    private String createPollyUrl() {
        final String prefix = this.getMyPolly().webInterface().isSSL() 
                ? "https://" : "http://"; //$NON-NLS-1$ //$NON-NLS-2$
        final String host = prefix + this.getMyPolly().webInterface().getPublicHost() + 
                ":" + this.getMyPolly().webInterface().getPort(); //$NON-NLS-1$
        return host;
    }
}
