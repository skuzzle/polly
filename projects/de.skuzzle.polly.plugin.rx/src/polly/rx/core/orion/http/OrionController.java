package polly.rx.core.orion.http;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import polly.rx.MSG;
import polly.rx.core.AZEntryManager;
import polly.rx.core.orion.LoginCode;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.PortalDecorator;
import polly.rx.core.orion.PortalProvider;
import polly.rx.core.orion.PortalProviderDecorator;
import polly.rx.core.orion.QuadrantProvider;
import polly.rx.core.orion.QuadrantProviderDecorator;
import polly.rx.core.orion.QuadrantUtils;
import polly.rx.core.orion.WormholeProvider;
import polly.rx.core.orion.WormholeProviderDecorator;
import polly.rx.core.orion.model.AlienRace;
import polly.rx.core.orion.model.AlienSpawn;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.QuadrantDecorator;
import polly.rx.core.orion.model.Resources;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorDecorator;
import polly.rx.core.orion.model.SectorType;
import polly.rx.core.orion.model.Wormhole;
import polly.rx.core.orion.model.WormholeDecorator;
import polly.rx.core.orion.model.json.FromClientSector;
import polly.rx.core.orion.model.json.OrionJsonAdapter;
import polly.rx.core.orion.pathplanning.PathPlanner;
import polly.rx.core.orion.pathplanning.PathPlanner.Group;
import polly.rx.core.orion.pathplanning.PathPlanner.HighlightedQuadrant;
import polly.rx.core.orion.pathplanning.PathPlanner.UniversePath;
import polly.rx.core.orion.pathplanning.RouteOptions;
import polly.rx.entities.DBAlienRace;
import polly.rx.parsing.ParseException;
import polly.rx.parsing.QuadrantCnPParser;
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
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;
import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.tools.collections.TemporaryValueMap;
import de.skuzzle.polly.tools.io.FastByteArrayInputStream;
import de.skuzzle.polly.tools.io.FastByteArrayOutputStream;

public class OrionController extends PollyController {

    public final static String VIEW_ORION_PREMISSION = "polly.permission.VIEW_ORION"; //$NON-NLS-1$
    public final static String WRITE_ORION_PREMISSION = "polly.permission.WRITE_ORION"; //$NON-NLS-1$
    public final static String ROUTE_ORION_PREMISSION = "polly.permission.ROUTE_ORION"; //$NON-NLS-1$

    public final static String MANAGE_RACE_PERMISSION = "polly.permission.MANAGE_ALIEN_RACES"; //$NON-NLS-1$

    public final static String PAGE_ORION = "/pages/orion"; //$NON-NLS-1$
    public final static String PAGE_QUAD_LAYOUT = "/pages/orion/quadlayout"; //$NON-NLS-1$
    public final static String PAGE_ALIEN_MANAGEMENT = "/pages/orion/manageAliens"; //$NON-NLS-1$
    public final static String PAGE_PORTALS = "/pages/orion/portals"; //$NON-NLS-1$
    
    public final static String API_GET_QUADRANT = "/api/orion/quadrant"; //$NON-NLS-1$
    public final static String API_GET_SECTOR_INFO = "/api/orion/sector"; //$NON-NLS-1$
    public final static String API_SET_ROUTE_TO = "/api/orion/routeTo"; //$NON-NLS-1$
    public final static String API_SET_ROUTE_FROM = "/api/orion/routeFrom"; //$NON-NLS-1$
    public final static String API_GET_ROUTE = "/api/orion/getRoute"; //$NON-NLS-1$
    public final static String API_POST_LAYOUT = "/api/orion/postLayout"; //$NON-NLS-1$
    public final static String API_SHARE_ROUTE = "/api/orion/share"; //$NON-NLS-1$
    public final static String API_GET_NTH_ROUTE = "/api/orion/nthRoute"; //$NON-NLS-1$
    public final static String API_GET_GROUP_IMAGE = "/api/orion/groupImage"; //$NON-NLS-1$
    public final static String API_JSON_QUADRANT = "/api/orion/json/quadrant"; //$NON-NLS-1$
    public final static String API_JSON_SECTOR = "/api/orion/json/sector"; //$NON-NLS-1$
    public final static String API_JSON_ROUTE = "/api/orion/json/route"; //$NON-NLS-1$
    public final static String API_JSON_POST_SECTOR = "/api/orion/json/postSector"; //$NON-NLS-1$
    public final static String API_SUBMIT_CODE = "/api/orion/get/loginCode"; //$NON-NLS-1$
    public final static String API_REQUEST_CODE = "/api/orion/json/requestCode"; //$NON-NLS-1$
    public final static String API_REMOVE_RACE = "/api/removeRace"; //$NON-NLS-1$
    public final static String API_ADD_RACE = "/api/addRace"; //$NON-NLS-1$
    public final static String API_ADD_SPAWN = "/api/addSpawn"; //$NON-NLS-1$
    public final static String API_REMOVE_SPAWN = "/api/orion/removeSpawn"; //$NON-NLS-1$
    
    private final static String CONTENT_QUAD_LAYOUT = "/polly/rx/httpv2/view/orion/quadlayout.html"; //$NON-NLS-1$
    private final static String CONTENT_QUADRANT = "/polly/rx/httpv2/view/orion/quadrant.html"; //$NON-NLS-1$
    private final static String CONTENT_SECTOR_INFO = "/polly/rx/httpv2/view/orion/sec_info.html"; //$NON-NLS-1$
    private final static String CONTENT_ORION = "/polly/rx/httpv2/view/orion/orion.html"; //$NON-NLS-1$
    private final static String CONTENT_ALIEN_MANAGER = "/polly/rx/httpv2/view/orion/alien.manager.html"; //$NON-NLS-1$
    private final static String CONTENT_ROUTE = "/polly/rx/httpv2/view/orion/route.html"; //$NON-NLS-1$
    private final static String CONTENT_ROUTE_SINGLE = "/polly/rx/httpv2/view/orion/route.single.html"; //$NON-NLS-1$
    private final static String CONTENT_SHARE_ROUTE = "/polly/rx/httpv2/view/orion/route.share.html"; //$NON-NLS-1$
    private final static String CONTENT_PORTALS = "/polly/rx/httpv2/view/portals.overview.html"; //$NON-NLS-1$
    
    private final static String REVORIX_CATEGORY_KEY = "httpRxCategory"; //$NON-NLS-1$
    private final static String ORION_NAME_KEY = "htmlOrionName"; //$NON-NLS-1$
    private final static String ORION_DESC_KEY = "htmlOrionDesc"; //$NON-NLS-1$
    private final static String ALIEN_MANAGEMENT_NAME_KEY = "htmlOrionAlienManagement"; //$NON-NLS-1$
    private final static String ALIEN_MANAGEMENT_DESC_KEY = "htmlOrionAlienManagementDesc"; //$NON-NLS-1$
    private final static String PORTALS_NAME_KEY = "htmlPortals"; //$NON-NLS-1$
    private final static String PORTALS_DESC_KEY = "htmlPortalsDesc"; //$NON-NLS-1$
    
    
    private final static String ROUTE_FROM_KEY = "routeFrom"; //$NON-NLS-1$
    private final static String ROUTE_TO_KEY = "routeTo"; //$NON-NLS-1$
    private final static String ROUTE_OPTIONS_KEY = "routeOptions"; //$NON-NLS-1$
    private final static String ROUTE_COUNT_KEY = "routeCount"; //$NON-NLS-1$

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

    public final static class DisplayPortal extends PortalDecorator {

        public DisplayPortal(Portal wrapped) {
            super(wrapped);
        }



        @Override
        public Sector getSector() {
            return new DisplaySector(super.getSector());
        }
    }

    public final static class DisplayPortalProvider extends PortalProviderDecorator {

        public DisplayPortalProvider(PortalProvider wrapped) {
            super(wrapped);
        }



        private List<? extends Portal> decorate(final List<? extends Portal> portals) {
            return new AbstractList<Portal>() {

                @Override
                public Portal get(int idx) {
                    return new DisplayPortal(portals.get(idx));
                }



                @Override
                public int size() {
                    return portals.size();
                }
            };
        }



        @Override
        public Portal getClanPortal(String nameOrTag) {
            final Portal p = super.getClanPortal(nameOrTag);
            if (p == null) {
                return null;
            }
            return new DisplayPortal(p);
        }



        @Override
        public Portal getPersonalPortal(String ownerName) {
            final Portal p = super.getPersonalPortal(ownerName);
            if (p == null) {
                return null;
            }
            return new DisplayPortal(p);
        }



        @Override
        public List<? extends Portal> getPortals(Sector sector, PortalType type) {
            return this.decorate(super.getPortals(sector, type));
        }



        @Override
        public List<? extends Portal> getPortals(Quadrant quadrant, PortalType type) {
            return this.decorate(super.getPortals(quadrant, type));
        }



        @Override
        public List<? extends Portal> getPortals(Sector sector) {
            return this.decorate(super.getPortals(sector));
        }
    }

    public final static class DisplayWormholeProvider extends WormholeProviderDecorator {

        public DisplayWormholeProvider(WormholeProvider wrapped) {
            super(wrapped);
        }



        private List<Wormhole> decorate(final List<Wormhole> holes) {
            return new AbstractList<Wormhole>() {

                @Override
                public Wormhole get(int index) {
                    return new WormholeDecorator(holes.get(index)) {

                        @Override
                        public Sector getSource() {
                            return new DisplaySector(super.getSource());
                        }



                        @Override
                        public Sector getTarget() {
                            return new DisplaySector(super.getTarget());
                        }
                    };
                }



                @Override
                public int size() {
                    return holes.size();
                }
            };
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



        private List<Quadrant> decorate(final List<? extends Quadrant> quadrants) {
            return new AbstractList<Quadrant>() {

                @Override
                public Quadrant get(int index) {
                    return new DisplayQuadrant(quadrants.get(index));
                }



                @Override
                public int size() {
                    return quadrants.size();
                }
            };
        }



        @Override
        public List<Quadrant> getAllQuadrants() {
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

    
    
    
    private class RouteContext {
        private final String routeId;
        private final Sector start;
        private final Sector target;
        private final RouteOptions options;
        private final List<UniversePath> paths;
        
        public RouteContext(String routeId, Sector start, Sector target, RouteOptions options,
                List<UniversePath> paths) {
            super();
            this.routeId = routeId;
            this.start = start;
            this.target = target;
            this.options = options;
            this.paths = paths;
        }
        
        
        
        public FastByteArrayOutputStream getQuadImage(int n, final int groupId) {
            final UniversePath p = this.paths.get(n - 1);
            final Group g = p.getGroups().stream()
                    .filter(grp -> grp.getId() == groupId)
                    .findFirst().get();                    
            
            final BufferedImage quadImg = QuadrantUtils.createQuadImage(g.getQuadrant());
            final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
            try {
                ImageIO.write(quadImg, "png", out); //$NON-NLS-1$
            } catch (IOException e) {
                e.printStackTrace();
            }
            return out;
        }

        

        public void putInto(Map<String, Object> c, int n) {
            c.put("routeId", this.routeId); //$NON-NLS-1$
            c.put("start", this.start); //$NON-NLS-1$
            c.put("target", this.target); //$NON-NLS-1$
            c.put("options", this.options); //$NON-NLS-1$
            c.put("path", this.paths.get(n - 1)); //$NON-NLS-1$
            c.put("n", n); //$NON-NLS-1$
            c.put("routeCount", this.paths.size()); //$NON-NLS-1$
        }
    }
    
    

    private final static long ROUTE_CACHE_TIME = Milliseconds.fromMinutes(5);
    private final static Random RANDOM = new Random();
    
    private final static TemporaryValueMap<String, RouteContext> ROUTES = 
            new TemporaryValueMap<>(ROUTE_CACHE_TIME);
    

    private final QuadrantProvider quadProvider;
    private final WormholeProvider holeProvider;
    private final PortalProvider portalProvider;

    private final PathPlanner pathPlanner;
    private final AZEntryManager azManager;



    public OrionController(MyPolly myPolly, AZEntryManager azManager) {
        this(myPolly, new DisplayQuadrantProvider(Orion.INSTANCE.getQuadrantProvider()),
                new DisplayWormholeProvider(Orion.INSTANCE.getWormholeProvider()),
                new DisplayPortalProvider(Orion.INSTANCE.getPortalProvider()),
                Orion.INSTANCE.getPathPlanner(), azManager);
    }



    /**
     * Copy Constructor for {@link #createInstance()}
     * 
     * @param myPolly
     * @param quadProvider
     * @param holeProvider
     * @param portalProvider
     * @param planner
     * @param azManager
     */
    private OrionController(MyPolly myPolly, QuadrantProvider quadProvider,
            WormholeProvider holeProvider, PortalProvider portalProvider,
            PathPlanner planner, AZEntryManager azManager) {
        super(myPolly);
        this.quadProvider = quadProvider;
        this.holeProvider = holeProvider;
        this.portalProvider = portalProvider;
        this.pathPlanner = planner;
        this.azManager = azManager;
    }



    @Override
    protected Controller createInstance() {
        return new OrionController(this.getMyPolly(), this.quadProvider,
                this.holeProvider, this.portalProvider, this.pathPlanner, this.azManager);
    }



    @Override
    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = super.createContext(content);
        HTMLTools.gainFieldAccess(c, MSG.class, "MSG"); //$NON-NLS-1$
        c.put("Messages", Constants.class); //$NON-NLS-1$
        return c;
    }



    @Get(API_SUBMIT_CODE)
    @Deprecated
    public HttpAnswer submitCode(@Param("code") String code, @Param("user") String user,
            @Param("pw") String pw) throws AlternativeAnswerException {
        this.checkLogin(user, pw);
        /*if (Orion.INSTANCE.getLoginCodeManager().updateCurrentCode(code)) {
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        }*/
        return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
    }



    @Get(API_REQUEST_CODE)
    public HttpAnswer requestCode(@Param("user") String user, @Param("pw") String pw)
            throws AlternativeAnswerException {
        //this.checkLogin(user, pw);
        final LoginCode code = Orion.INSTANCE.getLoginCodeManager().getCurrentCode();
        return new GsonHttpAnswer(200, code);
    }



    @Get(value = PAGE_ORION, name = ORION_NAME_KEY)
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, MSG.FAMILY, REVORIX_CATEGORY_KEY,
            ORION_DESC_KEY, VIEW_ORION_PREMISSION })
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
        c.put("routeStart", this.getSession().get(ROUTE_FROM_KEY)); //$NON-NLS-1$
        c.put("routeTarget", this.getSession().get(ROUTE_TO_KEY)); //$NON-NLS-1$
        c.put("personalPortals", Orion.INSTANCE.getPersonalPortals(this.getSessionUser())); //$NON-NLS-1$
        c.put("entryPortals", this.quadProvider.getEntryPortals()); //$NON-NLS-1$
        c.put("entries", this.azManager.getEntries(this.getSessionUser().getId())); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = PAGE_PORTALS, name = PORTALS_NAME_KEY)
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, MSG.FAMILY, REVORIX_CATEGORY_KEY,
            PORTALS_DESC_KEY, VIEW_ORION_PREMISSION })
    public HttpAnswer portals() throws AlternativeAnswerException {
        this.requirePermissions(VIEW_ORION_PREMISSION);
        return this.makeAnswer(this.createContext(CONTENT_PORTALS));
    }



    @Get(value = PAGE_QUAD_LAYOUT, name = ORION_NAME_KEY)
    //@OnRegister({ WebinterfaceManager.ADD_SUB_ENTRY, "Orion", MSG.FAMILY, ORION_DESC_KEY,
    //        VIEW_ORION_PREMISSION })
    public HttpAnswer quadLayout() throws AlternativeAnswerException {
        this.requirePermissions(OrionController.WRITE_ORION_PREMISSION);
        return this.makeAnswer(this.createContext(CONTENT_QUAD_LAYOUT));
    }



    @Get(value = PAGE_ALIEN_MANAGEMENT, name = ALIEN_MANAGEMENT_NAME_KEY)
    @OnRegister({ WebinterfaceManager.ADD_SUB_ENTRY, "Orion", MSG.FAMILY,
            ALIEN_MANAGEMENT_DESC_KEY, MANAGE_RACE_PERMISSION })
    public HttpAnswer alienManagement() throws AlternativeAnswerException {
        this.requirePermissions(OrionController.MANAGE_RACE_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_ALIEN_MANAGER);
        c.put("allRaces", Orion.INSTANCE.getAlienManager().getAllRaces()); //$NON-NLS-1$
        return this.makeAnswer(c);
    }


    
    @Get(API_REMOVE_RACE)
    public HttpAnswer removeRace(@Param("id") int id) throws AlternativeAnswerException {
        this.requirePermissions(OrionController.MANAGE_RACE_PERMISSION);
        final AlienRace ar = Orion.INSTANCE.getAlienManager().getRaceById(id);
        
        try {
            Orion.INSTANCE.getAlienManager().removeRace(ar);
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        } catch (OrionException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }

    
    
    public final static class AddRaceResult extends SuccessResult {
        public final int id;
        
        private AddRaceResult(int id) {
            super(true, ""); //$NON-NLS-1$
            this.id = id;
        }
    }
    
    
    @Get(API_ADD_RACE)
    public HttpAnswer addRace(@Param("name") String name, 
            @Param(value = "type", optional = true) String type, 
            @Param("aggr") boolean aggressive) throws AlternativeAnswerException {
        this.requirePermissions(MANAGE_RACE_PERMISSION);
        
        try {
            final DBAlienRace race = (DBAlienRace) Orion.INSTANCE.getAlienManager().addRace(
                    name, type, aggressive);
            return new GsonHttpAnswer(200, new AddRaceResult(race.getId()));
        } catch (OrionException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    @Get(API_ADD_SPAWN)
    public HttpAnswer addSpawn(@Param("name") String name, @Param("raceId") int raceId, 
            @Param("sector") String sectorString) {
        
        try {
            final Sector s = QuadrantUtils.parse(sectorString);
            final AlienRace race = Orion.INSTANCE.getAlienManager().getRaceById(raceId);
            
            Orion.INSTANCE.getAlienManager().addSpawn(name, race, s);
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        } catch (Exception e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    @Get(API_REMOVE_SPAWN)
    public HttpAnswer removeSpawn(@Param("id") int spawnId) throws AlternativeAnswerException {
        this.requirePermissions(MANAGE_RACE_PERMISSION);
        try {
            final AlienSpawn spawn = Orion.INSTANCE.getAlienManager().getSpawnById(spawnId);
            Orion.INSTANCE.getAlienManager().removeAlienSpawn(spawn);
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        } catch (Exception e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
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
            boolean showInfo, boolean abstractView) {
        Quadrant q = this.quadProvider.getQuadrant(quadName);
        if (showInfo) {
            final List<Wormhole> holes = this.holeProvider.getWormholes(q,
                    this.quadProvider);
            final Collection<? extends Portal> pportals = this.portalProvider.getPortals(
                    q, PortalType.PRIVATE);
            final Collection<? extends Portal> cportals = this.portalProvider.getPortals(
                    q, PortalType.CLAN);

            // TODO: this should use an alien manager which returns DisplaySectors within 
            //       the alien spawns
            final Collection<? extends AlienSpawn> spawns = 
                    Orion.INSTANCE.getAlienManager().getSpawnsByQuadrant(q.getName());
            
            if (abstractView) {
                q = this.createAbstract(q, holes, spawns);
            }
            
            
            final Resources hourlyProduction = QuadrantUtils.calculateHourlyProduction(q);
            final DecimalFormat nf = (DecimalFormat) DecimalFormat
                    .getInstance(Locale.ENGLISH);
            nf.applyPattern("0.00"); //$NON-NLS-1$

            c.put("holes", holes); //$NON-NLS-1$
            c.put("pportals", pportals); //$NON-NLS-1$
            c.put("cportals", cportals); //$NON-NLS-1$
            c.put("spawns", spawns); //$NON-NLS-1$
            c.put("nf", nf); //$NON-NLS-1$
            c.put("hourlyProduction", hourlyProduction.getAmountArray()); //$NON-NLS-1$
        }
        c.put("showQuadInfo", showInfo); //$NON-NLS-1$
        c.put("abstract", abstractView); //$NON-NLS-1$
        c.put("quad", q); //$NON-NLS-1$
    }
    
    
    
    private Quadrant createAbstract(Quadrant quad, Collection<? extends Wormhole> holes, 
            Collection<? extends AlienSpawn> spawns) {
        final HighlightedQuadrant q = new HighlightedQuadrant(quad, true);
        holes.forEach(h -> q.highlight(h.getSource(), SectorType.HIGHLIGHT_WH_START));
        spawns.forEach(s -> q.highlight(s.getSector(), SectorType.HIGHLIGHT_ALIEN_SPAWN));
        return q;
    }



    @Get(API_GET_QUADRANT)
    public HttpAnswer quadrant(@Param("quadName") String name, 
            @Param(value = "abstract", defaultValue = "false", optional = true) boolean renderAbstract) {

        if (!this.getMyPolly().roles()
                .hasPermission(this.getSessionUser(), VIEW_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }

        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        this.fillQuadrantContext(name, c, true, renderAbstract);
        return HttpAnswers.newTemplateAnswer(CONTENT_QUADRANT, c);
    }



    @Get(API_GET_QUADRANT)
    public HttpAnswer quadrant(@Param("quadName") String name, @Param("hlX") int hlX,
            @Param("hlY") int hlY) {

        if (!this.getMyPolly().roles()
                .hasPermission(this.getSessionUser(), VIEW_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }

        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        this.fillQuadrantContext(name, c, true, false);
        c.put("hlX", hlX); //$NON-NLS-1$
        c.put("hlY", hlY); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_QUADRANT, c);
    }



    @Get(API_GET_SECTOR_INFO)
    public HttpAnswer sectorInfo(@Param("quadrant") String quadrant, @Param("x") int x,
            @Param("y") int y) {

        if (!this.getMyPolly().roles()
                .hasPermission(this.getSessionUser(), VIEW_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }

        final Quadrant q = this.quadProvider.getQuadrant(quadrant);
        final Sector sector = q.getSector(x, y);
        final Map<String, Object> c = this.createContext(CONTENT_SECTOR_INFO);
        c.put("sector", sector); //$NON-NLS-1$
        if (sector != null) {
            final List<Wormhole> holes = this.holeProvider.getWormholes(sector,
                    this.quadProvider);
            final Collection<? extends Portal> pportals = this.portalProvider.getPortals(
                    sector, PortalType.PRIVATE);
            final Collection<? extends Portal> cportals = this.portalProvider.getPortals(
                    sector, PortalType.CLAN);
            c.put("pportals", pportals); //$NON-NLS-1$
            c.put("cportals", cportals); //$NON-NLS-1$
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
        if (!this.getMyPolly().roles()
                .hasPermission(this.getSessionUser(), ROUTE_ORION_PREMISSION)) {
            return new GsonHttpAnswer(403, new SuccessResult(false, MSG.httpNoPermission));
        }
        final Quadrant q = this.quadProvider.getQuadrant(quadrant);
        final Sector sector = q.getSector(x, y);
        if (sector != null) {
            this.getSession().set(key, sector);
            return new GsonHttpAnswer(200, new SectorResult(sector));
        }
        this.getSession().set(key, sector);
        return new GsonHttpAnswer(200, new SuccessResult(false, "")); //$NON-NLS-1$
    }



    @Get(API_SET_ROUTE_FROM)
    public HttpAnswer setRouteFrom(@Param("quadrant") String quadrant, @Param("x") int x,
            @Param("y") int y) {

        return this.updateRouteInformation(quadrant, x, y, ROUTE_FROM_KEY);
    }



    @Get(API_SET_ROUTE_TO)
    public HttpAnswer setRouteTo(@Param("quadrant") String quadrant, @Param("x") int x,
            @Param("y") int y) {

        return this.updateRouteInformation(quadrant, x, y, ROUTE_TO_KEY);
    }



    @Get(API_SHARE_ROUTE)
    public HttpAnswer shareRoute(@Param("routeId") String routeId) {
        final RouteContext rc = ROUTES.get(routeId);
        if (rc == null) {
            return HttpAnswers.newStringAnswer(MSG.htmlOrionRouteTimeOut);
        }
        
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        rc.putInto(c, 1);
        c.put("legend", SectorType.HIGHLIGHTS); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_SHARE_ROUTE, c);
    }



    @Get(API_GET_NTH_ROUTE)
    public HttpAnswer getNthRoute(@Param("id") String routeId, @Param("n") int n) {
        final RouteContext rc = ROUTES.get(routeId);
        if (rc == null) {
            return HttpAnswers.newStringAnswer(MSG.htmlOrionRouteTimeOut);
        }
        
        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        rc.putInto(c, n);
        return HttpAnswers.newTemplateAnswer(CONTENT_ROUTE_SINGLE, c);
    }



    @Get(API_GET_ROUTE)
    public HttpAnswer getRoute(
            @Param("fleetId") int fleetId,
            @Param(value = "jt", optional = true) String jt,
            @Param(value = "cjt", optional = true) String cjt,
            @Param(value = "bt", optional = true, defaultValue = "false") boolean blockTail,
            @Param(value = "be", optional = true, defaultValue = "false") boolean blockEntryPortals,
            @Param(value = "re", optional = true, defaultValue = "true") boolean renderDark) {

        if (!this.getMyPolly().roles()
                .hasPermission(this.getSessionUser(), ROUTE_ORION_PREMISSION)) {
            return HttpAnswers.newStringAnswer(403, MSG.httpNoPermission);
        }

        final Map<String, Object> c = this.createContext(""); //$NON-NLS-1$
        final HttpSession s = this.getSession();

        final List<Sector> personalPortals = Orion.INSTANCE.getPersonalPortals(this
                .getSessionUser());

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
        final Sector start = (Sector) s.get(ROUTE_FROM_KEY);
        final Sector target = (Sector) s.get(ROUTE_TO_KEY);

        final TimespanType jumpTime;
        if (fleetId == -1) {
            jumpTime = this.parse(jt, new TimespanType(0));
        } else {
            jumpTime = this.azManager.getJumpTime(fleetId, this.getSessionUser());
        }
        final TimespanType currentJumpTime = this.parse(cjt, jumpTime);
        final RouteOptions options = new RouteOptions(jumpTime, currentJumpTime,
                personalPortals, blockTail, blockEntryPortals, renderDark);
        final List<UniversePath> path = this.pathPlanner.findShortestPaths(start,
                target, options);

        s.set(ROUTE_OPTIONS_KEY, options);
        s.set(ROUTE_COUNT_KEY, path.size());

        final String routeId = createRouteId(); 
        final RouteContext rc = new RouteContext(routeId, start, target, options, 
                path);
        
        rc.putInto(c, 1);
        c.put("legend", SectorType.HIGHLIGHTS); //$NON-NLS-1$
        ROUTES.put(routeId, rc);
        return HttpAnswers.newTemplateAnswer(CONTENT_ROUTE, c);
    }

    
    
    private static String createRouteId() {
        synchronized (ROUTES) {
            String s = ""; //$NON-NLS-1$
            do {
                final long l = RANDOM.nextLong();
                s = Long.toHexString(l);
            } while (ROUTES.containsKey(s));
            return s;
        }
    }
    


    @Get(API_GET_GROUP_IMAGE)
    public HttpAnswer getImageForGroup(@Param("routeId") String routeId, 
            @Param("n") int n, @Param("grp") int id) {
        final RouteContext rc = ROUTES.get(routeId);
        if (rc == null) {
            return HttpAnswers.newStringAnswer(404, ""); //$NON-NLS-1$
        }
        
        final FastByteArrayOutputStream out = rc.getQuadImage(n, id);
        final InputStream in = new FastByteArrayInputStream(out);
        return new HttpInputStreamAnswer(200, in);
    }



    private TimespanType parse(String jumpTime, TimespanType alternative) {
        final Types types = this.getMyPolly().parse(jumpTime);
        if (types == null || !(types instanceof TimespanType)) {
            return alternative;
        }
        return (TimespanType) types;
    }



    @Get(API_JSON_ROUTE)
    public HttpAnswer getJsonRoute() {
        return HttpAnswers.newStringAnswer(""); //$NON-NLS-1$
    }



    @Get(API_JSON_SECTOR)
    public HttpAnswer getJsonSector(@Param("q") String quadName, @Param("x") int x,
            @Param("y") int y) {

        final Quadrant quad = this.quadProvider.getQuadrant(quadName);
        final Sector sector = quad.getSector(x, y);
        return OrionJsonAdapter.sectorAnswer(sector);
    }



    @Get(API_JSON_QUADRANT)
    public HttpAnswer getJsonQuadrant(@Param("q") String name) {
        final Quadrant quad = this.quadProvider.getQuadrant(name);
        return OrionJsonAdapter.quadrantAnswer(quad);
    }



    @Post(API_JSON_POST_SECTOR)
    public HttpAnswer postJson() {
        final String json = this.getEvent().getRequestBody();
        final FromClientSector sector = OrionJsonAdapter.readSectorFromClient(json);
        final String reporter = sector.getSelf();

        final PersistenceManagerV2 persistence = this.getMyPolly().persistence();
        persistence.writeAtomicParallel(new Atomic() {

            @Override
            public void perform(Write write) throws DatabaseException {
                try {
                    Orion.INSTANCE.getQuadrantUpdater().updateSectorInformation(
                            Collections.singleton(sector));
                    Orion.INSTANCE.getPortalUpdater().updatePortals(reporter, sector,
                            sector.getClanPortals());
                    Orion.INSTANCE.getPortalUpdater().updatePortals(reporter, sector,
                            sector.getPersonalPortals());
                    Orion.INSTANCE.getFleetTracker().updateOrionFleets(reporter,
                            sector.getOwnFleets());
                    Orion.INSTANCE.getFleetTracker().updateFleets(reporter,
                            sector.getFleets());
                } catch (OrionException e) {
                    throw new DatabaseException(e);
                }
            }
        });

        return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
    }
}
