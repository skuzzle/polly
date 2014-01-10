package polly.rx.core.orion.datasource;

import de.skuzzle.polly.sdk.PersistenceManagerV2;


public final class DBOrionAccess {

    
    private final DBQuadrantProvider quadProvider;
    private final DBQuadrantUpdater quadUpdater;
    private final DBPortalProvider portalProvider;
    private final DBPortalUpdater portalUpdater;
    
    
    public DBOrionAccess(PersistenceManagerV2 persistence) {
        this.quadProvider = new DBQuadrantProvider(persistence);
        this.quadUpdater = new DBQuadrantUpdater(persistence);
        this.portalProvider = new DBPortalProvider(persistence);
        this.portalUpdater = new DBPortalUpdater(persistence, this.quadUpdater);
        
        this.quadUpdater.addQuadrantListener(this.quadProvider);
    }
    
    
    
    
    public DBQuadrantProvider getQuadrantProvider() {
        return this.quadProvider;
    }
    
    
    
    public DBQuadrantUpdater getQuadrantUpdater() {
        return this.quadUpdater;
    }
    
    
    
    public DBPortalProvider getPortalProvider() {
        return this.portalProvider;
    }
    
    
    
    public DBPortalUpdater getPortalUpdater() {
        return this.portalUpdater;
    }
}
