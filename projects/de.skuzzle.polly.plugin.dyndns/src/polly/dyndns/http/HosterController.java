package polly.dyndns.http;

import java.util.Map;

import polly.dyndns.MSG;
import polly.dyndns.MyPlugin;
import polly.dyndns.core.DynDNSUpdater;
import polly.dyndns.core.HostManager;
import polly.dyndns.core.PublicIpFinder;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;


public class HosterController extends PollyController {
    
    private final static String PAGE_HOSTERS = "/pages/hosters"; //$NON-NLS-1$
    private final static String API_ADD_HOSTER = "/api/addHoster"; //$NON-NLS-1$
    private final static String API_REFRESH = "/api/refresh"; //$NON-NLS-1$
    private final static String API_DELETE_HOSTER = "/api/deleteHoster"; //$NON-NLS-1$
    private final static String API_ADD_ACCOUNT = "/api/addAccount"; //$NON-NLS-1$
    private final static String API_DELETE_ACCOUNT = "/api/deleteAccount"; //$NON-NLS-1$
    
    
    
    private final static String HOSTERS_CONTENT = "polly/dyndns/http/view/hosters.html"; //$NON-NLS-1$

    private final static String HOSTERS_CATEGORY_KEY = "category"; //$NON-NLS-1$
    private final static String HOSTERS_DESCRIPTION_KEY = "hosterDescription"; //$NON-NLS-1$
    private final static String HOSTERS_NAME_KEY = "hosterName"; //$NON-NLS-1$
    
    
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
        c.put("allHosters", this.hostManager.getAllHosters()); //$NON-NLS-1$
        c.put("allAccounts", this.hostManager.getAllAccounts()); //$NON-NLS-1$
        c.put("lastUpdate", this.ipFinder.getLastUpdate()); //$NON-NLS-1$
        c.put("currentIp", this.ipFinder.getLastKnownIp()); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_REFRESH)
    public HttpAnswer refresh() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.DYN_DNS_PERMISSION);
        this.ipFinder.updateNow();
        return HttpAnswers.newRedirectAnswer(PAGE_HOSTERS);
    }

    
    
    @Post(API_ADD_HOSTER)
    public HttpAnswer addHost(
            @Param("hosterName") String hosterName,
            @Param("updateUrl") String updateUrl) throws HttpException {
        this.requirePermissions(MyPlugin.DYN_DNS_PERMISSION);
        try {
            this.hostManager.addHoster(hosterName, updateUrl);
        } catch (DatabaseException e) {
            throw new HttpException(e);
        }
        return HttpAnswers.newRedirectAnswer(PAGE_HOSTERS);
    }
    
    
    
    @Post(API_ADD_ACCOUNT)
    public HttpAnswer addAccount(
            @Param("hosterId") int hosterId, 
            @Param("domainName") String domainName,
            @Param("userName") String userName,
            @Param("password") String password) throws HttpException {
        this.requirePermissions(MyPlugin.DYN_DNS_PERMISSION);
        try {
            this.hostManager.addAccount(hosterId, userName, domainName, password);
        } catch (DatabaseException e) {
            throw new HttpException(e);
        }
        
        return HttpAnswers.newRedirectAnswer(PAGE_HOSTERS);
    }
    
    
    
    @Get(API_DELETE_HOSTER)
    public HttpAnswer deleteHoster(@Param("hosterId") int id) throws HttpException {
        this.requirePermissions(MyPlugin.DYN_DNS_PERMISSION);
        try {
            this.hostManager.deleteHoster(id);
        } catch (DatabaseException e) {
            throw new HttpException(e);
        }
        return HttpAnswers.newRedirectAnswer(PAGE_HOSTERS);
    }
    
    
    
    @Get(API_DELETE_ACCOUNT)
    public HttpAnswer deleteAccount(@Param("accountId") int id) throws HttpException {
        this.requirePermissions(MyPlugin.DYN_DNS_PERMISSION);
        try {
            this.hostManager.deleteAccount(id);
        } catch (DatabaseException e) {
            throw new HttpException(e);
        }
        return HttpAnswers.newRedirectAnswer(PAGE_HOSTERS);
    }
}
