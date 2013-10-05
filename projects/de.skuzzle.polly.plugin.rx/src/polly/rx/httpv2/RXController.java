package polly.rx.httpv2;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polly.rx.MyPlugin;
import polly.rx.core.FleetDBManager;
import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.graphs.NamedPoint;
import polly.rx.httpv2.StatisticsGatherer.BattleReportStatistics;
import polly.rx.parsing.BattleReportParser;
import polly.rx.parsing.FleetScanParser;
import polly.rx.parsing.ParseException;
import polly.rx.parsing.QBattleReportParser;
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
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.streams.FastByteArrayInputStream;


public class RXController extends PollyController {

    private final FleetDBManager fleetDb;
    private final ScoreBoardManager sbManager;
    
    public RXController(MyPolly myPolly, FleetDBManager fleetDb, 
                ScoreBoardManager sbManager) {
        super(myPolly);
        this.fleetDb = fleetDb;
        this.sbManager = sbManager;
    }
    
    

    @Override
    protected Controller createInstance() {
        return new RXController(this.getMyPolly(), this.fleetDb, this.sbManager);
    }
    
    
    
    @Get(value = "/pages/fleetScanPage", name = "Fleet Scans")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Revorix", "List all fleet scans",
        FleetDBManager.VIEW_FLEET_SCAN_PERMISSION })
    public HttpAnswer fleetScanPage() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        return this.makeAnswer(this.createContext(
                "polly/rx/httpv2/view/fleetscans.overview.html"));
    }
    
    
    
    @Get("/pages/fleetScanDetails")
    public HttpAnswer fleetScanDetailsPage(@Param("scanId") int scanId) 
                throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        final FleetScan scan = this.fleetDb.getScanById(scanId);
        final Map<String, Object> c = this.createContext(
                "polly/rx/httpv2/view/fleetscan.details.html");
        c.put("scan", scan);
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = "/pages/fleetScanShips", name = "Scanned Ships")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Revorix", "List all scanned  ships",
        FleetDBManager.VIEW_FLEET_SCAN_PERMISSION })
    public HttpAnswer fleetScanShipsPage() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        return this.makeAnswer(this.createContext("polly/rx/httpv2/view/scanships.overview.html"));
    }
    
    
    
    @Get("/pages/scanShipDetails")
    public HttpAnswer scanShipDetails(@Param("shipId") int id) 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        final FleetScanShip ship = this.fleetDb.getShipByRevorixId(id);
        final Map<String, Object> c = this.createContext("polly/rx/httpv2/view/scanship.details.html");
        c.put("ship", ship);
        return this.makeAnswer(c);
    }
    
    
    
    
    public final class CompareList extends SuccessResult {
        private Set<String> entries;
        
        
        public CompareList() {
            super(true, "");
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
    
    
    
    @Get("/api/addToCompare")
    public HttpAnswer addToCompare(@Param("venadName") String name) 
                throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        if (cl == null) {
            cl = new CompareList();
            this.getSession().set("COMPARE_LIST", cl);
        }
        synchronized (cl) {
            cl.getEntries().add(name);
        }
        return HttpAnswers.newStringAnswer("").redirectTo("/pages/scoreboard");
    }
    
    
    
    @Get("/api/removeFromCompare")
    public HttpAnswer removeFromCompare(@Param("venadName") String name) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        if (cl != null) {
            synchronized (cl) {
                cl.getEntries().remove(name);
            }
        }
        return HttpAnswers.newStringAnswer("").redirectTo("/pages/scoreboard");
    }
    
    
    
    @Get(value = "/pages/scoreboard", name = "Scoreboard")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Revorix", "View and manage posted scoreboard entries",
        MyPlugin.SBE_PERMISSION })
    public HttpAnswer scoreboardPage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final Map<String, Object> c = this.createContext("polly/rx/httpv2/view/scoreboard.overview.html");
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        if (cl == null) {
            cl = new CompareList();
            this.getSession().set("COMPARE_LIST", cl);
        }
        c.put("compareList", cl);
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = "/pages/reports", name = "Battlereports")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Revorix", "View statistics about battle reports",
        FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION })
    public HttpAnswer reportsPage() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        final Map<String, Object> c = this.createContext(
                "polly/rx/httpv2/view/battlereports.overview.html");
        return this.makeAnswer(c);
    }
    
    
    
    @Get("/pages/score/compare")
    public HttpAnswer compare() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final Map<String, Object> c = this.createContext(
                "polly/rx/httpv2/view/scoreboard.compare.html");
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        c.put("compareList", cl);
        return this.makeAnswer(c);
    }
    
    
    
    @Get("/pages/score/details")
    public HttpAnswer venadDetails(@Param("venadName") String name) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final Map<String, Object> c = this.createContext(
                "polly/rx/httpv2/view/scoreboard.details.html");
        c.put("venad", name);
        return this.makeAnswer(c);
    }
    
    
    
    @Post("/api/postFleetScan")
    public HttpAnswer postFleetScan(
            @Param("scan") String scan,
            @Param("quadrant") String quadrant,
            @Param("x") int x,
            @Param("y") int y,
            @Param(value = "meta", treatEmpty = true) String meta) 
                    throws AlternativeAnswerException {
        
        this.requirePermissions(FleetDBManager.ADD_FLEET_SCAN_PERMISSION);
        
        try {
            final FleetScan fs = FleetScanParser.parseFleetScan(
                    scan, quadrant, x, y, meta);
            this.fleetDb.addFleetScan(fs);
        } catch (ParseException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        return new GsonHttpAnswer(200, new SuccessResult(true, "Scan added"));
    }
    
    
    
    @Get("/api/graphCompare")
    public HttpAnswer graphCompare(@Param("maxMonth") int maxMonths) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        if (cl == null) {
            return HttpAnswers.newStringAnswer("");
        }
        final Collection<NamedPoint> allPoints = new ArrayList<>();
        final InputStream graph = this.sbManager.createMultiGraph(maxMonths, allPoints, 
                cl.getEntries().toArray(new String[0]));
        graph.mark(Integer.MAX_VALUE);
        
        final String imgName = "graph_compare" + "_mm_" + maxMonths; 
        this.getSession().set(imgName, graph);
        final Map<String, Object> c = this.createContext("");
        c.put("allPoints", allPoints);
        c.put("imgName", imgName);
        c.put("maxMonths", maxMonths);
        c.put("isCompare", true);
        return HttpAnswers.newTemplateAnswer("polly/rx/httpv2/view/graph.html", c);
    }
    
    
    
    @Get("/api/graphForVenad")
    public HttpAnswer graphForVenad(@Param("venadName") String name, 
            @Param("maxMonth") int maxMonths) throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        
        final List<ScoreBoardEntry> entries = this.sbManager.getEntries(name);
        Collections.sort(entries, ScoreBoardEntry.BY_DATE);
        
        final Collection<NamedPoint> allPoints = new ArrayList<>();
        final InputStream graph = this.sbManager.createLatestGraph(entries, maxMonths, 
                allPoints);
        graph.mark(Integer.MAX_VALUE);
        
        final String imgName = "graph_" + name + "_mm_" + maxMonths; 
        this.getSession().set(imgName, graph);
        final Map<String, Object> c = this.createContext("");
        c.put("allPoints", allPoints);
        c.put("venad", name);
        c.put("imgName", imgName);
        c.put("maxMonths", maxMonths);
        return HttpAnswers.newTemplateAnswer("polly/rx/httpv2/view/graph.html", c);
    }
    
    
    
    @Get("/api/imageFromSession")
    public HttpAnswer imageFromSession(@Param("imgName") String imgName) 
            throws AlternativeAnswerException, IOException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final FastByteArrayInputStream in = (FastByteArrayInputStream) 
                this.getSession().getAttached(imgName);
        synchronized (in) {
            in.reset();
        }
        return new HttpInputStreamAnswer(200, in);
    }
    
    
    
    @Get("/polly/rx/httpv2/view")
    public HttpAnswer getFile() {
        final ClassLoader cl = this.getClass().getClassLoader();
        return new HttpResourceAnswer(200, cl, this.getEvent().getPlainUri());
    }
    
    
    
    @Post("/api/postScoreboard")
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
        return HttpAnswers.newStringAnswer("").redirectTo("/pages/scoreboard");
    }
    
    
    
    @Post("/postScoreboard")
    public HttpAnswer postScoreboardExt(
            @Param("user") String user, 
            @Param("pw") String pw,
            @Param("paste") String paste) throws AlternativeAnswerException {
        
        final User u = this.getMyPolly().users().getUser(user);
        if (u == null || !u.checkPassword(pw)) {
            return new GsonHttpAnswer(200, new SuccessResult(false, "Illegal login"));
        } else if (!this.getMyPolly().roles().hasPermission(u, MyPlugin.SBE_PERMISSION)) {
            return new GsonHttpAnswer(200, new SuccessResult(false, "No permission"));
        }
        
        try {
            final Collection<ScoreBoardEntry> entries =
                    ScoreBoardParser.parse(paste, Time.currentTime());
            this.sbManager.addEntries(entries);
            return new GsonHttpAnswer(200, new SuccessResult(true, entries.size() + 
                    " entries sent to polly"));
        } catch (ParseException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        
    }
    
    
    
    @Get("/pages/reportDetails")
    public HttpAnswer reportDetails(@Param("reportId") int reportId) 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
        
        final BattleReport br = this.fleetDb.getReportById(reportId);
        final Map<String, Object> c = this.createContext(
                "polly/rx/httpv2/view/battlereports.details.html");
        
        c.put("report", br);
        c.put("df", new DecimalFormat("0.##"));
        c.put("fleetDb", this.fleetDb);
        c.put("Math", Math.class);
        this.prepareContext(br.getAttackerShips(), "Attacker", c);
        this.prepareContext(br.getDefenderShips(), "Defender", c);
        
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
        
        c.put("pzDamage" + postfix, pzDamage);
        c.put("maxPzDamage" + postfix, maxPzDamage);
        c.put("minPzDamage" + postfix, minPzDamage);
        c.put("avgPzDamage" + postfix, avgPzDamage);
        c.put("shieldDamage" + postfix, shieldDamage);
        c.put("maxShieldDamage" + postfix, maxShieldDamage);
        c.put("minShieldDamage" + postfix, minShieldDamage);
        c.put("avgShieldDamage" + postfix, avgShieldDamage);
        c.put("capiXp" + postfix, capiXp);
        c.put("maxCapiXp" + postfix, maxCapiXp);
        c.put("minCapiXp" + postfix, minCapiXp);
        c.put("avgCapiXp" + postfix, avgCapiXp);
        c.put("crewXp" + postfix, crewXp);
        c.put("maxCrewXp" + postfix, maxCrewXp);
        c.put("minCrewXp" + postfix, minCrewXp);
        c.put("avgCrewXp" + postfix, avgCrewXp);
        c.put("maxWend" + postfix, maxWend);
        c.put("minWend" + postfix, minWend);
    }
    
    
    @Post("/api/postReport")
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
        return new GsonHttpAnswer(200, new SuccessResult(true, "Report added"));
    }
    
    
    
    @Post("/postQReport")
    public HttpAnswer postQReport(
            @Param("user") String user, 
            @Param("pw") String pw, 
            @Param("report") String report) {
        
        final User u = this.getMyPolly().users().getUser(user);
        if (u == null || !u.checkPassword(pw)) {
            return new GsonHttpAnswer(200, new SuccessResult(false, "Illegal login"));
        } else if (!this.getMyPolly().roles().hasPermission(u, 
                    FleetDBManager.ADD_BATTLE_REPORT_PERMISSION)) {
            return new GsonHttpAnswer(200, new SuccessResult(false, "No permission"));
        }
        
        try {
            final BattleReport br = QBattleReportParser.parse(report, u.getId());
            this.fleetDb.addBattleReport(br);
        } catch (ParseException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        return new GsonHttpAnswer(200, new SuccessResult(true, "Report added"));
    }
    
    
    
    @Get("/api/deleteReport")
    public HttpAnswer deleteReport(@Param("reportId") int id) 
            throws DatabaseException, AlternativeAnswerException {
        
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                FleetDBManager.DELETE_BATTLE_REPORT_PERMISSION)) {
            return new GsonHttpAnswer(200, new SuccessResult(false, "No permission"));
        }
        this.fleetDb.deleteReportById(id);
        return new GsonHttpAnswer(200, new SuccessResult(true, ""));
    }
    
    
    
    @Get("/api/battlereportStatistics")
    public HttpAnswer battleReportStatistics() {
        final User user = this.getSessionUser();
        final String STATISTIC_KEY = "BR_STATS_" + user.getName();
        BattleReportStatistics stats = 
                (BattleReportStatistics) this.getSession().getAttached(STATISTIC_KEY);
        
        if (stats == null) {
            stats = new BattleReportStatistics();
        }
        
        synchronized (stats) {
            final DecimalFormat df = new DecimalFormat("#.##");
            final Map<String, Object> c = this.createContext("");
            c.put("df", df);
            c.put("capiXpSumAttacker", stats.capiXpSumAttacker);
            c.put("crewXpSumAttacker", stats.crewXpSumAttacker);
            c.put("capiXpSumDefender", stats.capiXpSumDefender);
            c.put("crewXpSumDefender", stats.crewXpSumDefender);
            c.put("pzDamageAttacker", stats.pzDamageAttacker);
            c.put("pzDamageDefender", stats.pzDamageDefender);
            c.put("repairTimeAttacker", stats.repairTimeAttacker);
            c.put("repairTimeDefender", stats.repairTimeDefender);
            c.put("repairCostDefender", stats.repairCostDefender);
            c.put("repairCostAttacker", stats.repairCostAttacker);
            c.put("kwAttacker", stats.kwAttacker);
            c.put("kwDefender", stats.kwDefender);
            
            c.put("artifacts", stats.artifacts);
            c.put("chance", stats.artifactChance * 100.0);
            
            c.put("dropSum", stats.dropSum);
            c.put("dropMax", stats.dropMax);
            c.put("dropMin", stats.dropMin);
            c.put("reportSize", stats.reportSize);
            return HttpAnswers.newTemplateAnswer(
                    "polly/rx/httpv2/view/battlereports.statistics.html", c);
        }
    }
    
    
    
    @Get("/GM/scrapescoreboarddata.user.js")
    public HttpAnswer installScoreboard() 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final String prefix = this.getMyPolly().webInterface().isSSL() 
                ? "https://" : "http://";
        final String host = prefix + this.getMyPolly().webInterface().getPublicHost() + 
                ":" + this.getMyPolly().webInterface().getPort();
        
        final Map<String, String> c = new HashMap<String, String>();
        c.put("host", host);
        return HttpAnswers.newTemplateAnswer(
                "polly/rx/httpv2/view/scrapescoreboarddata.user.js", c);
    }
    
    
    
    @Get("/GM/kbreport.user.js")
    public HttpAnswer installLiveKB() 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
        final String prefix = this.getMyPolly().webInterface().isSSL() 
                ? "https://" : "http://";
        final String host = prefix + this.getMyPolly().webInterface().getPublicHost() + 
                ":" + this.getMyPolly().webInterface().getPort();
        
        final Map<String, String> c = new HashMap<String, String>();
        c.put("api", "/postQReport");
        c.put("host", host);
        return HttpAnswers.newTemplateAnswer(
                "polly/rx/httpv2/view/kbreport.user.js", c);
    }
}
