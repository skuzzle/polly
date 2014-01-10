package polly.rx.httpv2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import polly.rx.MSG;
import polly.rx.core.AZEntryManager;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.QuadrantProvider;
import polly.rx.core.orion.QuadrantProviderDecorator;
import polly.rx.core.orion.QuadrantUtils;
import polly.rx.core.orion.WormholeProvider;
import polly.rx.core.orion.WormholeProviderDecorator;
import polly.rx.core.orion.model.OrionObjectUtil;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.QuadrantDecorator;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorDecorator;
import polly.rx.core.orion.model.SectorType;
import polly.rx.core.orion.model.Wormhole;
import polly.rx.core.orion.model.WormholeDecorator;
import polly.rx.core.orion.pathplanning.PathPlanner;
import polly.rx.core.orion.pathplanning.PathPlanner.Group;
import polly.rx.core.orion.pathplanning.PathPlanner.UniversePath;
import polly.rx.core.orion.pathplanning.RouteOptions;
import polly.rx.entities.RxRessource;
import polly.rx.parsing.ParseException;
import polly.rx.parsing.QuadrantCnPParser;
import polly.rx.parsing.VenadHelper;

import com.google.gson.Gson;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.answers.HttpInputStreamAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;
import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import de.skuzzle.polly.tools.io.FastByteArrayInputStream;
import de.skuzzle.polly.tools.io.FastByteArrayOutputStream;


public class OrionController extends PollyController {
    
    public final static String VIEW_ORION_PREMISSION = "polly.permission.VIEW_ORION"; //$NON-NLS-1$
    public final static String WRITE_ORION_PREMISSION = "polly.permission.WRITE_ORION"; //$NON-NLS-1$
    public final static String ROUTE_ORION_PREMISSION = "polly.permission.ROUTE_ORION"; //$NON-NLS-1$
    
    
    public final static String PAGE_ORION = "/pages/orion"; //$NON-NLS-1$
    public final static String PAGE_QUAD_LAYOUT = "/pages/orion/quadlayout"; //$NON-NLS-1$
    
    public final static String API_GET_QUADRANT = "/api/orion/quadrant"; //$NON-NLS-1$
    public final static String API_GET_SECTOR_INFO = "/api/orion/sector"; //$NON-NLS-1$
    public final static String API_SET_ROUTE_TO = "/api/orion/routeTo"; //$NON-NLS-1$
    public final static String API_SET_ROUTE_FROM = "/api/orion/routeFrom"; //$NON-NLS-1$
    public final static String API_GET_ROUTE = "/api/orion/getRoute"; //$NON-NLS-1$
    public final static String API_POST_LAYOUT = "/api/orion/postLayout"; //$NON-NLS-1$
    public final static String API_SHARE_ROUTE = "/api/orion/share"; //$NON-NLS-1$
    public final static String API_GET_NTH_ROUTE = "/api/orion/nthRoute";  //$NON-NLS-1$
    public final static String API_GET_GROUP_IMAGE = "/api/orion/groupImage"; //$NON-NLS-1$
    public final static String API_JSON_QUADRANT = "/api/orion/json/quadrant"; //$NON-NLS-1$
    public final static String API_JSON_SECTOR = "/api/orion/json/sector"; //$NON-NLS-1$
    public final static String API_JSON_ROUTE = "/api/orion/json/route"; //$NON-NLS-1$
    public final static String API_JSON_POST_SECTOR = "/api/orion/json/postSector"; //$NON-NLS-1$
    
    private final static String CONTENT_QUAD_LAYOUT = "/polly/rx/httpv2/view/orion/quadlayout.html"; //$NON-NLS-1$
    private final static String CONTENT_QUADRANT = "/polly/rx/httpv2/view/orion/quadrant.html"; //$NON-NLS-1$
    private final static String CONTENT_SECTOR_INFO = "/polly/rx/httpv2/view/orion/sec_info.html"; //$NON-NLS-1$
    private final static String CONTENT_ORION = "/polly/rx/httpv2/view/orion/orion.html"; //$NON-NLS-1$
    private final static String CONTENT_ROUTE = "/polly/rx/httpv2/view/orion/route.html"; //$NON-NLS-1$
    private final static String CONTENT_ROUTE_SINGLE = "/polly/rx/httpv2/view/orion/route.single.html"; //$NON-NLS-1$
    private final static String CONTENT_SHARE_ROUTE = "/polly/rx/httpv2/view/orion/route.share.html"; //$NON-NLS-1$
            
    private final static String REVORIX_CATEGORY_KEY = "httpRxCategory"; //$NON-NLS-1$
    private final static String ORION_NAME_KEY = "htmlOrionName"; //$NON-NLS-1$
    private final static String ORION_DESC_KEY = "htmlOrionDesc"; //$NON-NLS-1$
    
    private final static String ROUTE_FROM_KEY = "routeFrom"; //$NON-NLS-1$
    private final static String ROUTE_TO_KEY = "routeTo"; //$NON-NLS-1$
    private final static String ROUTE_N_KEY = "route_"; //$NON-NLS-1$
    private final static String ROUTE_OPTIONS_KEY = "routeOptions"; //$NON-NLS-1$
    private final static String ROUTE_COUNT_KEY = "routeCount"; //$NON-NLS-1$
    private final static String QUAD_IMAGE_KEY = "quadImg_"; //$NON-NLS-1$
    
    
    
    public final static class DisplaySector extends SectorDecorator {

        private DisplaySector(Sector wrapped) {
            super(wrapped);
        }
        
        public String getQuadId() {
            return this.getQuadName().replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    

    
    public final static class DisplayQuadrant extends QuadrantDecorator {

        private DisplayQuadrant(Quadrant wrapped) {
            super(wrapped);
        }
        
        
        public String getQuadId() {
            return this.getName().replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        @Override
        public Sector getSector(int x, int y) {
            return new DisplaySector(super.getSector(x, y));
        }
    }
    
    
    
    public final static class DisplayWormholeProvider extends WormholeProviderDecorator {

        public DisplayWormholeProvider(WormholeProvider wrapped) {
            super(wrapped);
        }
        
        private List<Wormhole> decorate(List<Wormhole> holes) {
            final List<Wormhole> result = new ArrayList<>(holes.size());
            for (final Wormhole hole : holes) {
                result.add(new WormholeDecorator(hole) { 
                    @Override
                    public Sector getSource() {
                        return new DisplaySector(super.getSource());
                    }
                    
                    @Override
                    public Sector getTarget() {
                        return new DisplaySector(super.getTarget());
                    }
                });
            }
            return result;
        }
        
        @Override
        public List<Wormhole> getWormholes(Quadrant quadrant, QuadrantProvider quads) {
            return this.decorate(super.getWormholes(quadrant, quads));
        }

        @Override
        public List<Wormhole> getWormholes(Sector sector, QuadrantProvider quads) {
            return this.decorate(super.getWormholes(sector, quads));
        }
    }
    
    
    
    public final static class DisplayQuadrantProvider extends QuadrantProviderDecorator {

        public DisplayQuadrantProvider(QuadrantProvider wrapped) {
            super(wrapped);
        }
        
        private Collection<DisplayQuadrant> decorate(Collection<? extends Quadrant> quadrants) {
            final List<DisplayQuadrant> result = new ArrayList<>();
            for (final Quadrant quadrant : quadrants) {
                result.add(new DisplayQuadrant(quadrant));
            }
            return result;
        }
        
        @Override
        public Collection<DisplayQuadrant> getAllQuadrants() {
            return this.decorate(super.getAllQuadrants());
        }
        
        @Override
        public Quadrant getQuadrant(Sector sector) {
            return new DisplayQuadrant(super.getQuadrant(sector));
        }
        
        @Override
        public Quadrant getQuadrant(String name) {
            name = name.replace('_', ' ');
            return new DisplayQuadrant(super.getQuadrant(name));
        }
    }
    
    
    
    private final QuadrantProvider quadProvider;
    private final WormholeProvider holeProvider;
    private final PathPlanner pathPlanner;
    private final AZEntryManager azManager;
    
    
    public OrionController(MyPolly myPolly, AZEntryManager azManager) {
        this(myPolly, 
                new DisplayQuadrantProvider(Orion.INSTANCE.getQuadrantProvider()),
                new DisplayWormholeProvider(Orion.INSTANCE.getWormholeProvider()),
                Orion.INSTANCE.getPathPlanner(),
                azManager);
    }
    
    
    
    /**
     * Copy Constructor for {@link #createInstance()}
     * @param myPolly
     * @param quadProvider
     * @param holeProvider
     * @param planner
     * @param azManager
     */
    private OrionController(MyPolly myPolly, QuadrantProvider quadProvider, 
            WormholeProvider holeProvider, PathPlanner planner, AZEntryManager azManager) {
        super(myPolly);
        this.quadProvider = quadProvider;
        this.holeProvider = holeProvider;
        this.pathPlanner = planner;
        this.azManager = azManager;
    }
    
    

    @Override
    protected Controller createInstance() {
        return new OrionController(this.getMyPolly(), this.quadProvider, 
                this.holeProvider, this.pathPlanner, this.azManager);
    }
    
    
    
    @Override
    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = super.createContext(content);
        HTMLTools.gainFieldAccess(c, MSG.class, "MSG"); //$NON-NLS-1$
        c.put("Messages", Constants.class); //$NON-NLS-1$
        return c;
    }
    
    
    
    @Get(value = PAGE_ORION, name = ORION_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        ORION_DESC_KEY,
        VIEW_ORION_PREMISSION })
    public HttpAnswer orion() throws AlternativeAnswerException {
        this.requirePermissions(VIEW_ORION_PREMISSION);
        
        final Map<String, Object> c = this.createContext(CONTENT_ORION);
        
        final Collection<Quadrant> allQuads = new TreeSet<>(new Comparator<Quadrant>() {
            @Override
            public int compare(Quadrant o1, Quadrant o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        allQuads.addAll(this.quadProvider.getAllQuadrants());
        c.put("allQuads", allQuads); //$NON-NLS-1$
        c.put("routeStart", this.getSession().getAttached(ROUTE_FROM_KEY)); //$NON-NLS-1$
        c.put("routeTarget", this.getSession().getAttached(ROUTE_TO_KEY)); //$NON-NLS-1$
        c.put("personalPortals", Orion.INSTANCE.getPersonalPortals(this.getSessionUser())); //$NON-NLS-1$
        c.put("entryPortals", this.quadProvider.getEntryPortals()); //$NON-NLS-1$
        c.put("entries", this.azManager.getEntries(this.getSessionUser().getId())); //$NON-NLS-1$
        c.put("legend", SectorType.HIGHLIGHTS); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = PAGE_QUAD_LAYOUT, name = ORION_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_SUB_ENTRY, 
        "Orion", 
        MSG.FAMILY,
        ORION_DESC_KEY,
        VIEW_ORION_PREMISSION })
    public HttpAnswer quadLayout() throws AlternativeAnswerException {
        this.requirePermissions(OrionController.WRITE_ORION_PREMISSION);
        return this.makeAnswer(this.createContext(CONTENT_QUAD_LAYOUT));
    }
    
    
    
    @Post(API_POST_LAYOUT)
    public HttpAnswer postQuadLayout(@Param("quadName") String quadName, 
            @Param("paste") String paste) throws HttpException {
        
        try {
            final Collection<Sector> sectors = QuadrantCnPParser.parse(paste, quadName);
            Orion.INSTANCE.getQuadrantUpdater().updateSectorInformation(sectors);
        } catch (ParseException | OrionException e) {
            throw new HttpException(e);
        }
        
        return HttpAnswers.newRedirectAnswer(PAGE_QUAD_LAYOUT);
    }
    
    
    
    private void fillQuadrantContext(String quadName, Map<String, Object> c, 
            boolean showInfo) {
        final Quadrant q = this.quadProvider.getQuadrant(quadName);
        
        if (showInfo) {
            final List<Wormhole> holes = this.holeProvider.getWormholes(
                    q, this.quadProvider);
            c.put("holes", holes); //$NON-NLS-1$
        }
        c.put("showQuadInfo", showInfo); //$NON-NLS-1$
        c.put("quad", q); //$NON-NLS-1$
    }
    
    
    
    @Get(API_GET_QUADRANT)
    public HttpAnswer quadrant(@Param("quadName") String name) {
        
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                VIEW_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }

        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        this.fillQuadrantContext(name, c, true);
        return HttpAnswers.newTemplateAnswer(CONTENT_QUADRANT, c);
    }
    
    
    
    @Get(API_GET_QUADRANT)
    public HttpAnswer quadrant(
            @Param("quadName") String name, 
            @Param("hlX") int hlX, 
            @Param("hlY") int hlY) {
        
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                VIEW_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }
        
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        this.fillQuadrantContext(name, c, true);
        c.put("hlX", hlX); //$NON-NLS-1$
        c.put("hlY", hlY); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_QUADRANT, c);
    }
    
    
    
    @Get(API_GET_SECTOR_INFO)
    public HttpAnswer sectorInfo(
            @Param("quadrant") String quadrant, 
            @Param("x") int x,
            @Param("y") int y) {
        
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                VIEW_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }
        
        final Sector sector = this.quadProvider.getQuadrant(quadrant).getSector(x, y);
        final Map<String, Object> c = this.createContext(CONTENT_SECTOR_INFO);
        c.put("sector", sector); //$NON-NLS-1$
        if (sector != null) {
            final List<Wormhole> holes = this.holeProvider.getWormholes(
                    sector, this.quadProvider);
            c.put("holes", holes); //$NON-NLS-1$
        }
        
        return HttpAnswers.newTemplateAnswer(CONTENT_SECTOR_INFO, c);
    }

    
    
    public class SectorResult extends SuccessResult {
        public final String quadName;
        public final int x;
        public final int y;
        
        public SectorResult(Sector sector) {
            super(true, ""); //$NON-NLS-1$
            this.quadName = sector.getQuadName();
            this.x = sector.getX();
            this.y = sector.getY();
        }
    }
    
    
    
    private HttpAnswer updateRouteInformation(String quadrant, int x, int y, String key) {
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                ROUTE_ORION_PREMISSION)) {
            return new GsonHttpAnswer(403, 
                    new SuccessResult(false, MSG.httpNoPermission));
        }
        final Quadrant q = this.quadProvider.getQuadrant(quadrant);
        final Sector sector = q.getSector(x, y);
        if (sector != null) {
            this.getSession().set(key, sector);
            return new GsonHttpAnswer(200, 
                new SectorResult(sector));
        }
        this.getSession().set(key, sector);
        return new GsonHttpAnswer(200, 
            new SuccessResult(false, "")); //$NON-NLS-1$
    }
    
    
    
    @Get(API_SET_ROUTE_FROM)
    public HttpAnswer setRouteFrom(
            @Param("quadrant") String quadrant, 
            @Param("x") int x,
            @Param("y") int y) {
        
        return this.updateRouteInformation(quadrant, x, y, ROUTE_FROM_KEY);
    }
    
    
    
    @Get(API_SET_ROUTE_TO)
    public HttpAnswer setRouteTo(
            @Param("quadrant") String quadrant, 
            @Param("x") int x,
            @Param("y") int y) {
        
        return this.updateRouteInformation(quadrant, x, y, ROUTE_TO_KEY);
    }
    
    
    
    @Get(API_SHARE_ROUTE)
    public HttpAnswer shareRoute(@Param("startQuad") String startQuad,
            @Param("startX") int startX,
            @Param("startY") int startY,
            @Param("targetQuad") String targetQuad,
            @Param("targetX") int targetX,
            @Param("targetY") int targetY,
            @Param(value = "jt", optional = true) String jumpTime,
            @Param(value = "cjt", optional = true) String currentJumpTime,
            @Param(value = "bt", optional = true, defaultValue = "false") boolean blockTail) {
        
        final HttpSession s = this.getSession();
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        final Sector start = this.quadProvider.getQuadrant(
                startQuad).getSector(startX, startY);
        final Sector target = this.quadProvider.getQuadrant(
                targetQuad).getSector(targetX, targetY);
        
        final TimespanType jt = this.parse(jumpTime, new TimespanType(0L));
        final TimespanType cjt = this.parse(currentJumpTime, jt);
        
        final List<Sector> personalPortals = 
                Orion.INSTANCE.getPersonalPortals(this.getSessionUser());
        final RouteOptions options = new RouteOptions(jt, cjt, personalPortals, blockTail);
        final Collection<UniversePath> path = this.pathPlanner.findShortestPaths(
                start, target, options);
        
        final Iterator<UniversePath> it = path.iterator();
        for (int i = 0; i < path.size(); ++i) {
            final UniversePath p = it.next();
            s.set(ROUTE_N_KEY + (i + 1), p);
            this.createImages(p);
        }
        s.set(ROUTE_OPTIONS_KEY, options);
        s.set(ROUTE_COUNT_KEY, path.size());
        
        c.put("start", start); //$NON-NLS-1$
        c.put("target", target); //$NON-NLS-1$
        c.put("options", options); //$NON-NLS-1$
        c.put("path", path.iterator().next()); //$NON-NLS-1$
        c.put("n", 1); //$NON-NLS-1$
        c.put("routeCount", path.size()); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_SHARE_ROUTE, c);
    }
    
    
    
    @Get(API_GET_NTH_ROUTE)
    public HttpAnswer getNthRoute(@Param("n") int n) {
        final HttpSession s = this.getSession();
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        c.put("start", s.getAttached(ROUTE_FROM_KEY)); //$NON-NLS-1$
        c.put("target", s.getAttached(ROUTE_TO_KEY)); //$NON-NLS-1$
        c.put("options", s.getAttached(ROUTE_OPTIONS_KEY)); //$NON-NLS-1$
        c.put("path", s.getAttached(ROUTE_N_KEY + n)); //$NON-NLS-1$
        c.put("n", n); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_ROUTE_SINGLE, c);
    }
    
    
    
    @Get(API_GET_ROUTE)
    public HttpAnswer getRoute(
            @Param("fleetId") int fleetId,
            @Param(value = "jt", optional = true) String jt,
            @Param(value = "cjt", optional = true) String cjt,
            @Param(value = "bt", optional = true, defaultValue = "false") boolean blockTail) {
        
        if (!this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                ROUTE_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }
        
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        final HttpSession s = this.getSession();
        
        final List<Sector> personalPortals = 
                Orion.INSTANCE.getPersonalPortals(this.getSessionUser());
        
        if (!s.isSet(ROUTE_FROM_KEY)) {
            if (!personalPortals.isEmpty()) {
                s.set(ROUTE_FROM_KEY, personalPortals.iterator().next());
            } else {
                return HttpAnswers.newStringAnswer(""); //$NON-NLS-1$    
            }
        }
        if (!s.isSet(ROUTE_TO_KEY)) {
            return HttpAnswers.newStringAnswer(""); //$NON-NLS-1$
        }
        final Sector start = (Sector) s.getAttached(ROUTE_FROM_KEY);
        final Sector target = (Sector) s.getAttached(ROUTE_TO_KEY);
        
        final TimespanType jumpTime;
        if (fleetId == -1) {
            jumpTime = this.parse(jt, new TimespanType(0));
        } else {
            jumpTime = this.azManager.getJumpTime(fleetId, this.getSessionUser());
        }
        final TimespanType currentJumpTime = this.parse(cjt, jumpTime);
        final RouteOptions options = new RouteOptions(jumpTime, currentJumpTime, 
                personalPortals, blockTail);
        final Collection<UniversePath> path = this.pathPlanner.findShortestPaths(
                start, target, options);
        
        final Iterator<UniversePath> it = path.iterator();
        for (int i = 0; i < path.size(); ++i) {
            final UniversePath p = it.next();
            s.set(ROUTE_N_KEY + (i + 1), p);
            this.createImages(p);
        }
        s.set(ROUTE_OPTIONS_KEY, options);
        s.set(ROUTE_COUNT_KEY, path.size());
        
        c.put("start", start); //$NON-NLS-1$
        c.put("target", target); //$NON-NLS-1$
        c.put("options", options); //$NON-NLS-1$
        c.put("path", path.iterator().next()); //$NON-NLS-1$
        c.put("n", 1); //$NON-NLS-1$
        c.put("routeCount", path.size()); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_ROUTE, c);
    }
    
    
    
    @Get(API_GET_GROUP_IMAGE)
    public HttpAnswer getImageForGroup(@Param("grp") int id) {
        final Object o = this.getSession().getAttached(QUAD_IMAGE_KEY + id);
        if (o == null) {
            return HttpAnswers.newStringAnswer(404, ""); //$NON-NLS-1$
        }
        final FastByteArrayOutputStream out = (FastByteArrayOutputStream) o;
        final InputStream in = new FastByteArrayInputStream(out);
        return new HttpInputStreamAnswer(200, in);
    }

    
    
    private void createImages(UniversePath path) {
        for (final Group g : path.getGroups()) {
            final BufferedImage quadImg = QuadrantUtils.createQuadImage(g.getQuadrant());
            final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
            try {
                ImageIO.write(quadImg, "png", out); //$NON-NLS-1$
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            this.getSession().set(QUAD_IMAGE_KEY + g.getId(), out);
        }
    }
    
    
    
    private TimespanType parse(String jumpTime, TimespanType alternative) {
        final Types types = this.getMyPolly().parse(jumpTime);
        if (types == null || !(types instanceof TimespanType)) {
            return alternative;
        }
        return (TimespanType) types;
    }
    
    
    
    public final static class JsonProduction implements Production {
        public final String ress;
        public final int ressId;
        public final float rate;
        
        // for deserialization
        public transient RxRessource rxRess;
        
        public JsonProduction(Production production) {
            super();
            this.ress = production.getRess().toString();
            this.rate = production.getRate();
            this.ressId = production.getRess().ordinal() + 1;
        }

        @Override
        public String toString() {
            return OrionObjectUtil.productionString(this);
        }
        
        @Override
        public int hashCode() {
            return OrionObjectUtil.productionHash(this);
        }
        
        @Override
        public final boolean equals(Object obj) {
            return EqualsHelper.testEquality(this, obj);
        }
        
        @Override
        public Class<?> getEquivalenceClass() {
            return Production.class;
        }

        @Override
        public boolean actualEquals(Equatable o) {
            return OrionObjectUtil.productionEquals(this, (Production) o);
        }

        @Override
        public RxRessource getRess() {
            return this.rxRess;
        }

        @Override
        public float getRate() {
            return this.rate;
        }
    }
    
    
    
    public final static class JsonSector implements Sector {
        public final String type;
        public final int x;
        public final int y;
        public final String quadName;
        public final String imgName;
        public final int attacker;
        public final int defender;
        public final int guard;
        public final JsonProduction[] production;
        
        // only for deserialization
        public final String[] personalPortals;
        public final String[] clanportals;
        public final String wormhole;
        
        // helper fields
        private transient Date date = Time.currentTime();
        private transient SectorType sectorType;
        
        public JsonSector(Sector src) {
            this.quadName = src.getQuadName();
            this.type = src.getType().toString();
            this.x = src.getX();
            this.y = src.getY();
            this.imgName = src.getType().getImgName();
            this.attacker = src.getAttackerBonus();
            this.defender = src.getDefenderBonus();
            this.guard = src.getSectorGuardBonus();
            this.production = new JsonProduction[src.getRessources().size()];
            this.sectorType = src.getType();
            int i = 0;
            for (final Production prod : src.getRessources()) {
                this.production[i++] = new JsonProduction(prod);
            }
            this.personalPortals = new String[0];
            this.clanportals = new String[0];
            this.wormhole = null;
        }

        @Override
        public String toString() {
            return OrionObjectUtil.sectorString(this);
        }
        
        @Override
        public int hashCode() {
            return OrionObjectUtil.sectorHash(this);
        }
        
        @Override
        public final boolean equals(Object obj) {
            return EqualsHelper.testEquality(this, obj);
        }

        @Override
        public Class<?> getEquivalenceClass() {
            return Sector.class;
        }

        @Override
        public boolean actualEquals(Equatable o) {
            return OrionObjectUtil.sectorsEqual(this, (Sector) o);
        }

        @Override
        public String getQuadName() {
            return this.quadName;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public int getAttackerBonus() {
            return this.attacker;
        }

        @Override
        public int getDefenderBonus() {
            return this.defender;
        }

        @Override
        public int getSectorGuardBonus() {
            return this.guard;
        }

        @Override
        public Date getDate() {
            if (this.date == null) {
                this.date = Time.currentTime();
            }
            return this.date;
        }


        @Override
        public SectorType getType() {
            return this.sectorType;
        }

        @Override
        public Collection<? extends Production> getRessources() {
            return Arrays.asList(this.production);
        }
    }
    
    
    
    private final static class PortalImpl implements Portal {

        private final String ownerName;
        private final String ownerClan;
        private final Sector sector;
        private final PortalType type;
        private final Date date;
        
        public PortalImpl(String ownerName, String ownerClan, Sector sector, 
                PortalType type) {
            Check.notNull(ownerName, ownerClan, sector, type);
            this.ownerName = ownerName;
            this.ownerClan = ownerClan;
            this.sector = sector;
            this.type = type;
            this.date = Time.currentTime();
        }
        
        @Override
        public String toString() {
            return OrionObjectUtil.portalString(this);
        }
        
        @Override
        public int hashCode() {
            return OrionObjectUtil.portalHash(this);
        }
        
        @Override
        public final boolean equals(Object obj) {
            return EqualsHelper.testEquality(this, obj);
        }
        
        @Override
        public Class<?> getEquivalenceClass() {
            return Portal.class;
        }

        @Override
        public boolean actualEquals(Equatable o) {
            return OrionObjectUtil.portalsEqual(this, (Portal) o);
        }

        @Override
        public Sector getSector() {
            return this.sector;
        }

        @Override
        public String getOwner() {
            return this.ownerName;
        }

        @Override
        public String getOwnerClan() {
            return this.ownerClan;
        }

        @Override
        public PortalType getType() {
            return this.type;
        }

        @Override
        public Date getDate() {
            return this.date;
        }
        
    }
    
    
    
    public final static class JsonQuadrant {
        public final String name;
        public final int maxX;
        public final int maxY;
        public final JsonSector[] sectors;
        
        public JsonQuadrant(Quadrant src) {
            this.name = src.getName();
            this.maxX = src.getMaxX();
            this.maxY = src.getMaxY();
            this.sectors = new JsonSector[src.getSectors().size()];
            int i = 0;
            for (final Sector s : src.getSectors()) {
                this.sectors[i++] = new JsonSector(s);
            }
        }
    }
    
    
    
    @Get(API_JSON_ROUTE)
    public HttpAnswer getJsonRoute() {
        return HttpAnswers.newStringAnswer(""); //$NON-NLS-1$
    }
    
    
    
    @Get(API_JSON_SECTOR)
    public HttpAnswer getJsonSector(@Param("q") String quadName,
            @Param("x") int x,
            @Param("y") int y) {
        
        final Quadrant quad = this.quadProvider.getQuadrant(quadName);
        final Sector sector = quad.getSector(x, y);
        final JsonSector jSector = new JsonSector(sector);
        return new GsonHttpAnswer(200, jSector);
    }
    
    
    
    @Get(API_JSON_QUADRANT)
    public HttpAnswer getJsonQuadrant(@Param("q") String name) {
        final Quadrant quad = this.quadProvider.getQuadrant(name);
        final JsonQuadrant jQuad = new JsonQuadrant(quad);
        return new GsonHttpAnswer(200, jQuad);
    }
    
    
    
    @Post(API_JSON_POST_SECTOR)
    public HttpAnswer postJson() {
        final String json = this.getEvent().getRequestBody();
        final Gson gson = new Gson();
        final JsonSector jSector = gson.fromJson(json, JsonSector.class);
        if (jSector.type == null) {
            return new GsonHttpAnswer(200, new SuccessResult(false, "")); //$NON-NLS-1$
        }
        final List<Portal> portals = new ArrayList<>();
        if (jSector.personalPortals != null) {
            for (final String portal : jSector.personalPortals) {
                final String ownerName = VenadHelper.getName(portal);
                final String ownerClan = VenadHelper.getClan(portal);
                portals.add(new PortalImpl(ownerName, ownerClan, jSector, PortalType.PRIVATE));
            }
        }
        if (jSector.clanportals != null) {
            for (final String portal : jSector.clanportals) {
                final String ownerName = VenadHelper.getName(portal);
                final String ownerClan = VenadHelper.getClan(portal);
                portals.add(new PortalImpl(ownerName, ownerClan, jSector, PortalType.CLAN));
            }
        }
        jSector.sectorType = SectorType.byName(jSector.type);
        for (final JsonProduction prod : jSector.production) {
            prod.rxRess = RxRessource.values()[prod.ressId - 1];
        }
        try {
            Orion.INSTANCE.getQuadrantUpdater().updateSectorInformation(
                    Collections.singleton(jSector));
            for (final Portal portal : portals) {
                Orion.INSTANCE.getPortalUpdater().updatePortal(portal);
            }
        } catch (OrionException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
        
        return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
    }
}
