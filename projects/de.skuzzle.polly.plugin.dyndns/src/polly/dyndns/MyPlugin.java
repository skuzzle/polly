package polly.dyndns;

import java.util.Set;

import polly.dyndns.core.DynDNSUpdater;
import polly.dyndns.core.HostManager;
import polly.dyndns.core.PublicIpFinder;
import polly.dyndns.entities.Hoster;
import polly.dyndns.http.HosterController;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;


public class MyPlugin extends PollyPlugin {
    
    public final static String DYN_DNS_PERMISSION = "polly.permission.dyndns";
    
    
    private PublicIpFinder ipFinder;
    private DynDNSUpdater updater;
    private HostManager manager;
    private HosterController controller;
    
    
    
    private final static int UPDATE_INTERVAL = 5; // 5 min
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
            DuplicatedSignatureException {
        super(myPolly);
        
        myPolly.persistence().registerEntity(Hoster.class);
    }
    
    
    
    @Override
    public void onLoad() throws PluginException {
        super.onLoad();
        
        this.manager = new HostManager(this.getMyPolly().persistence());
        this.ipFinder = new PublicIpFinder(this.getMyPolly().getLoggerName(PublicIpFinder.class), 
                UPDATE_INTERVAL);
        this.updater = new DynDNSUpdater(this.getMyPolly().getLoggerName(DynDNSUpdater.class), 
                this.manager);
        this.ipFinder.addIPChangedListener(this.updater);
        this.ipFinder.updateNow();
        
        this.controller = new HosterController(this.getMyPolly(), this.manager, 
                this.updater, this.ipFinder);
        this.getMyPolly().webInterface().getServer().addController(this.controller);
    }
    
    
    
    
    @Override
    public Set<String> getContainedPermissions() {
        final Set<String> s = super.getContainedPermissions();
        s.add(DYN_DNS_PERMISSION);
        return s;
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
    }
}
