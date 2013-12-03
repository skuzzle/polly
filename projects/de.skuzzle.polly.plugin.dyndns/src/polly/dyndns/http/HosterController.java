package polly.dyndns.http;

import java.util.Map;

import polly.dyndns.MSG;
import polly.dyndns.MyPlugin;
import polly.dyndns.core.DynDNSUpdater;
import polly.dyndns.core.HostManager;
import polly.dyndns.core.PublicIpFinder;
import polly.dyndns.entities.Hoster;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;
import de.skuzzle.polly.sdk.resources.Constants;


public class HosterController extends PollyController {
    
    private final static String PAGE_HOSTERS = "pages/hosters";
    private final static String API_ADD_HOSTER = "api/addHoster";
    private final static String API_REFRESH = "api/refresh";
    
    
    private final static String HOSTERS_CONTENT = "polly/dyndns/http/view/hosters.html";

    private final static String HOSTERS_CATEGORY_KEY = "category";
    private final static String HOSTERS_DESCRIPTION_KEY = "hosterDescription";
    private final static String HOSTERS_NAME_KEY = "hosterName";
    
    
    private final HostManager hostManager;
    private final DynDNSUpdater updater;
    private final PublicIpFinder ipFinder;
    
    
    
    public HosterController(MyPolly myPolly, HostManager hostManager, 
            DynDNSUpdater updater, PublicIpFinder finder) {
        super(myPolly);
        this.hostManager = hostManager;
        this.updater = updater;
        this.ipFinder = finder;
    }
    
    
    
    @Override
    protected Controller createInstance() {
        return new HosterController(this.getMyPolly(), this.hostManager, this.updater, 
                this.ipFinder);
    }
    
    
    
    @Override
    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = super.createContext(content);
        HTMLTools.gainFieldAccess(c, MSG.class, "MSG"); //$NON-NLS-1$
        c.put("Constants", Constants.class);
        return c;
    }
    
    
    
    @Get(value = PAGE_HOSTERS, name = HOSTERS_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        HOSTERS_CATEGORY_KEY, 
        HOSTERS_DESCRIPTION_KEY,
        MyPlugin.DYN_DNS_PERMISSION })
    public HttpAnswer hosterPage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.DYN_DNS_PERMISSION);
        final Map<String, Object> c = this.createContext(HOSTERS_CONTENT);
        c.put("allHosters", this.hostManager.getAllHosters());
        c.put("lastUpdate", this.ipFinder.getLastUpdate());
        c.put("currentIp", this.ipFinder.getLastKnownIp());
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_REFRESH)
    public HttpAnswer refresh() {
        this.ipFinder.updateNow();
        return HttpAnswers.newRedirectAnswer(PAGE_HOSTERS);
    }

    
    
    @Post(API_ADD_HOSTER)
    public HttpAnswer addHost(
            @Param("hosterName") String hosterName,
            @Param("hostName") String hostName,
            @Param("userName") String userName,
            @Param("password") String password,
            @Param("updateUrl") String updateUrl) {
        
        final Hoster h = new Hoster(hosterName, userName, hostName, password, updateUrl);
        try {
            this.hostManager.addHoster(h);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return HttpAnswers.newRedirectAnswer(PAGE_HOSTERS);
    }
}
