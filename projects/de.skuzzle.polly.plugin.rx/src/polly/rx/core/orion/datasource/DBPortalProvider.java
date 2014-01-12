package polly.rx.core.orion.datasource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import polly.rx.core.orion.PortalProvider;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.entities.DBPortal;
import polly.rx.entities.DBSector;

public class DBPortalProvider implements PortalProvider {

    private final PersistenceManagerV2 persistence;



    public DBPortalProvider(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }


    
    @Override
    public Collection<? extends Portal> getPortals(Quadrant quadrant, PortalType type) {
        return this.persistence.atomic().findList(DBPortal.class, 
                DBPortal.QUERY_PORTAL_BY_QUAD, 
                new Param(type, quadrant.getName()));
    }
    
    
    
    @Override
    public Collection<? extends Portal> getPortals(Sector sector, PortalType type) {
        try (final Read read = this.persistence.read()) {
            final DBSector s = read.findSingle(DBSector.class, 
                    DBSector.QUERY_FIND_SECTOR, 
                    new Param(sector.getQuadName(), sector.getX(), sector.getY()));
            
            if (s == null) {
                return Collections.emptyList();
            }
            final List<DBPortal> portals = read.findList(DBPortal.class, 
                    DBPortal.QUERY_PORTAL_BY_TYPE_AND_SECTOR, 
                    new Param(type, s));
            return portals;
        }
    }
    
    

    @Override
    public Collection<? extends Portal> getPortals(Sector sector) {
        try (final Read read = this.persistence.read()) {
            final DBSector s = read.findSingle(DBSector.class, 
                    DBSector.QUERY_FIND_SECTOR, 
                    new Param(sector.getQuadName(), sector.getX(), sector.getY()));
            
            if (s == null) {
                return Collections.emptyList();
            }
            final List<DBPortal> portals = read.findList(DBPortal.class, 
                    DBPortal.QUERY_PORTAL_BY_SECTOR, 
                    new Param(s));
            return portals;
        }
    }



    @Override
    public Portal getPersonalPortal(String ownerName) {
        return this.persistence.atomic().findSingle(DBPortal.class, 
                DBPortal.QUERY_PORTAL_BY_TYPE_AND_OWNER, 
                new Param(PortalType.PRIVATE, ownerName));
    }



    @Override
    public Portal getClanPortal(String nameOrTag) {
        try (final Read read = this.persistence.read()) {
            Portal p = read.findSingle(DBPortal.class, 
                    DBPortal.QUERY_PORTAL_BY_TYPE_AND_OWNER, 
                    new Param(PortalType.CLAN, nameOrTag));
            
            if (p == null) {
                p = read.findSingle(DBPortal.class, 
                        DBPortal.QUERY_PORTAL_BY_TYPE_AND_CLANTAG, 
                        new Param(PortalType.CLAN, nameOrTag));
            }
            return p;
        }
    }

}
