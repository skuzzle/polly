package polly.rx.httpv2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polly.rx.MyPlugin;
import polly.rx.core.FleetDBManager;
import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.graphs.NamedPoint;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.answers.HttpInputStreamAnswer;
import de.skuzzle.polly.http.api.answers.HttpResourceAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
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
    public HttpAnswer fleetScanPage() {
        return this.makeAnswer(this.createContext("http/view/fleetscans.overview.html"));
    }
    
    
    
    @Get("/pages/fleetScanDetails")
    public HttpAnswer fleetScanDetailsPage(@Param("scanId") int scanId) 
                throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        final FleetScan scan = this.fleetDb.getScanById(scanId);
        final Map<String, Object> c = this.createContext(
                "http/view/fleetscan.details.html");
        c.put("scan", scan);
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = "/pages/fleetScanShips", name = "Scanned Ships")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Revorix", "List all scanned  ships",
        FleetDBManager.VIEW_FLEET_SCAN_PERMISSION })
    public HttpAnswer fleetScanShipsPage() throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        return this.makeAnswer(this.createContext("http/view/scanships.overview.html"));
    }
    
    
    
    @Get("/pages/scanShipDetails")
    public HttpAnswer scanShipDetails(@Param("shipId") int id) 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        final FleetScanShip ship = this.fleetDb.getShipByRevorixId(id);
        final Map<String, Object> c = this.createContext("http/view/scanship.details.html");
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
    public HttpAnswer scoreboardPage() {
        final Map<String, Object> c = this.createContext("http/view/scoreboard.overview.html");
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        if (cl == null) {
            cl = new CompareList();
            this.getSession().set("COMPARE_LIST", cl);
        }
        c.put("compareList", cl);
        return this.makeAnswer(c);
    }
    
    
    
    @Get("/pages/compare")
    public HttpAnswer compare() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        final Map<String, Object> c = this.createContext("http/view/scoreboard.compare.html");
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        c.put("compareList", cl);
        return this.makeAnswer(c);
    }
    
    
    
    @Get("/api/compareImage")
    public HttpAnswer compareImage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        CompareList cl = (CompareList) this.getSession().getAttached("COMPARE_LIST");
        if (cl == null) {
            return HttpAnswers.newStringAnswer("").redirectTo("/pages/scoreboard");
        }
        final Collection<NamedPoint> allPoints = new ArrayList<>();
        final InputStream graph = this.sbManager.createMultiGraph(24, allPoints, 
                cl.getEntries().toArray(new String[0]));
        return new HttpInputStreamAnswer(200, graph);
    }
    
    
    
    @Get("/api/graphForVenad")
    public HttpAnswer graphForVenad(@Param("venadName") String name) 
                throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.SBE_PERMISSION);
        
        final List<ScoreBoardEntry> entries = this.sbManager.getEntries(name);
        Collections.sort(entries, ScoreBoardEntry.BY_DATE);
        final ScoreBoardEntry oldest = entries.get(0);
        final ScoreBoardEntry youngest = entries.get(entries.size() - 1);        
        
        final Collection<NamedPoint> allPoints = new ArrayList<>();
        final InputStream graph = this.sbManager.createLatestGraph(entries, 24, 
                allPoints);
        graph.mark(Integer.MAX_VALUE);
        
        this.getSession().set("graph_" + name, graph);
        final Map<String, Object> c = this.createContext("");
        c.put("allPoints", allPoints);
        c.put("venad", name);
        c.put("imgName", "graph_" + name);
        return HttpAnswers.newTemplateAnswer("/http/view/graph.html", c);
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
}
