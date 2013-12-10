package polly.rx.httpv2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import polly.rx.MSG;
import polly.rx.core.orion.Quadrant;
import polly.rx.core.orion.QuadrantManager;
import polly.rx.core.orion.Wormhole;
import polly.rx.entities.QuadSector;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;


public class OrionController extends PollyController {
    
    public final static String VIEW_ORION_PREMISSION = "polly.permission.VIEW_ORION"; //$NON-NLS-1$
    public final static String WRITE_ORION_PREMISSION = "polly.permission.WRITE_ORION"; //$NON-NLS-1$
    
    
    public final static String PAGE_ORION = "/pages/orion"; //$NON-NLS-1$
    
    public final static String API_GET_QUADRANT = "/api/orion/quadrant"; //$NON-NLS-1$
    public final static String API_GET_SECTOR_INFO = "/api/orion/sector"; //$NON-NLS-1$
    
    private final static String CONTENT_QUADRANT = "/polly/rx/httpv2/view/orion/quadrant.html"; //$NON-NLS-1$
    private final static String CONTENT_SECTOR_INFO = "/polly/rx/httpv2/view/orion/sec_info.html"; //$NON-NLS-1$
    private final static String CONTENT_ORION = "/polly/rx/httpv2/view/orion/orion.html"; //$NON-NLS-1$
    
    private final static String REVORIX_CATEGORY_KEY = "httpRxCategory"; //$NON-NLS-1$
    private final static String ORION_NAME_KEY = "httpOrionName"; //$NON-NLS-1$
    private final static String ORION_DESC_KEY = "httpOrionDesc"; //$NON-NLS-1$
    
    
    private final QuadrantManager qManager;
    
    public OrionController(MyPolly myPolly, QuadrantManager qManager) {
        super(myPolly);
        this.qManager = qManager;
    }
    
    

    @Override
    protected Controller createInstance() {
        return new OrionController(this.getMyPolly(), this.qManager);
    }
    
    
    
    @Override
    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = super.createContext(content);
        HTMLTools.gainFieldAccess(c, MSG.class, "MSG"); //$NON-NLS-1$
        return c;
    }
    
    
    
    @Get(value = PAGE_ORION, name = ORION_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        REVORIX_CATEGORY_KEY, 
        ORION_DESC_KEY,
        VIEW_ORION_PREMISSION })
    public HttpAnswer orion() {
        final Map<String, Object> c = this.createContext(CONTENT_ORION);
        final Collection<String> allQuads = this.qManager.getQuadrants();
        c.put("allQuads", allQuads); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_GET_QUADRANT)
    public HttpAnswer quadrant(@Param("name") String name) 
            throws AlternativeAnswerException {
        this.requirePermissions(VIEW_ORION_PREMISSION);
        final Quadrant q = this.qManager.createQuadrant(name);
        final Map<String, Object> c = this.createContext(CONTENT_QUADRANT);
        c.put("quad", q); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_GET_SECTOR_INFO)
    public HttpAnswer sectorInfo(
            @Param("quadrant") String quadrant, 
            @Param("x") int x,
            @Param("y") int y) throws AlternativeAnswerException {
        
        this.requirePermissions(VIEW_ORION_PREMISSION);
        
        final QuadSector sector = this.qManager.getSector(quadrant, x, y);
        
        final Map<String, Object> c = this.createContext(CONTENT_SECTOR_INFO);
        if (sector != null) {
            final List<Wormhole> holes = this.qManager.getWormholes(sector);
            c.put("holes", holes); //$NON-NLS-1$
        }
        
        return this.makeAnswer(c);
    }

}
