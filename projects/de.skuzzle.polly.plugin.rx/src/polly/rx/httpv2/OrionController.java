package polly.rx.httpv2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import polly.rx.MSG;
import polly.rx.core.orion.QuadrantProvider;
import polly.rx.core.orion.WormholeProvider;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Wormhole;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
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
    
    
    private final QuadrantProvider quadProvider;
    private final WormholeProvider holeProvider;
    
    
    
    public OrionController(MyPolly myPolly, QuadrantProvider quadProvider, 
            WormholeProvider holeProvider) {
        super(myPolly);
        this.quadProvider = quadProvider;
        this.holeProvider = holeProvider;
    }
    
    

    @Override
    protected Controller createInstance() {
        return new OrionController(this.getMyPolly(), 
                this.quadProvider, this.holeProvider);
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
        final Collection<String> allQuads = this.quadProvider.getAllQuadrantNames();
        c.put("allQuads", allQuads); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_GET_QUADRANT)
    public HttpAnswer quadrant(@Param("quadName") String name) 
            throws AlternativeAnswerException {
        this.requirePermissions(VIEW_ORION_PREMISSION);
        final Quadrant q = this.quadProvider.getQuadrant(name);
        final Map<String, Object> c = this.createContext("");
        c.put("quad", q); //$NON-NLS-1$
        return HttpAnswers.newTemplateAnswer(CONTENT_QUADRANT, c);
    }
    
    
    
    @Get(API_GET_SECTOR_INFO)
    public HttpAnswer sectorInfo(
            @Param("quadrant") String quadrant, 
            @Param("x") int x,
            @Param("y") int y) throws AlternativeAnswerException {
        
        this.requirePermissions(VIEW_ORION_PREMISSION);
        
        final Sector sector = this.quadProvider.getQuadrant(quadrant).getSector(x, y);
        
        final Map<String, Object> c = this.createContext(CONTENT_SECTOR_INFO);
        if (sector != null) {
            final List<Wormhole> holes = this.holeProvider.getWormholes(
                    sector, this.quadProvider);
            c.put("holes", holes); //$NON-NLS-1$
        }
        
        return this.makeAnswer(c);
    }

}
