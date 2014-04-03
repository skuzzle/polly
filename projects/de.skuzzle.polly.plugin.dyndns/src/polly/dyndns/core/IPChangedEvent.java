package polly.dyndns.core;

import de.skuzzle.jeve.Event;


public class IPChangedEvent extends Event<PublicIpFinder> {

    private final String currentIp;
    
    public IPChangedEvent(PublicIpFinder source, String currentIp) {
        super(source);
        this.currentIp = currentIp;
    }
    
    
    
    public String getCurrentIp() {
        return this.currentIp;
    }
}
