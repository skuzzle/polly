package polly.rx.core.orion.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.time.Time;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.PortalUpdater;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.Sector;
import polly.rx.entities.DBPortal;
import polly.rx.entities.DBSector;

public class DBPortalUpdater implements PortalUpdater {

    private final PersistenceManagerV2 persistence;
    private final DBQuadrantUpdater quadUpdater;

    

    public DBPortalUpdater(PersistenceManagerV2 persistence, 
            DBQuadrantUpdater quadUpdater) {
        this.persistence = persistence;
        this.quadUpdater = quadUpdater;
    }



    @Override
    public synchronized Collection<DBPortal> updatePortals(
            Collection<? extends Portal> portals) throws OrionException {
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();
            final List<DBPortal> result = new ArrayList<>(portals.size());
            
            for (final Portal newPortal : portals) {
                final Sector current = newPortal.getSector();
                DBSector sector = read.findSingle(DBSector.class, 
                        DBSector.QUERY_FIND_SECTOR, 
                        new Param(current.getQuadName(), current.getX(), current.getY()));
                
                if (sector == null) {
                    // if target sector doesn't exist, create it
                    final Collection<DBSector> updates = 
                            this.quadUpdater.updateSectorInformation(
                                    Collections.singleton(newPortal.getSector()));
                    sector = updates.iterator().next();
                }
                
                
                final DBPortal existing = read.findSingle(DBPortal.class, 
                        DBPortal.QUERY_PORTAL_BY_TYPE_AND_OWNER, 
                        new Param(newPortal.getType(), newPortal.getOwner()));
                
                if (existing != null && !existing.equals(newPortal)) {
                    // portal exists and differs from new portal
                    // move it to new sector
                    existing.setSector(sector);
                } else if (existing == null) {
                    // new portal needs to be added
                    final DBPortal portal = new DBPortal(
                            newPortal.getOwner(), 
                            newPortal.getOwnerClan(), 
                            newPortal.getType(), 
                            sector, 
                            Time.currentTime());
                    write.single(portal);
                    result.add(portal);
                }
    
                result.add(existing);
            }
            return result;
        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
    }

}
