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
import de.skuzzle.polly.tools.events.Dispatch;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.EventProviders;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.PortalEvent;
import polly.rx.core.orion.PortalListener;
import polly.rx.core.orion.PortalUpdater;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.Sector;
import polly.rx.entities.DBPortal;
import polly.rx.entities.DBSector;

public class DBPortalUpdater implements PortalUpdater {

    private final PersistenceManagerV2 persistence;
    private final DBQuadrantUpdater quadUpdater;
    private final EventProvider events;



    public DBPortalUpdater(PersistenceManagerV2 persistence, DBQuadrantUpdater quadUpdater) {
        this.persistence = persistence;
        this.quadUpdater = quadUpdater;
        this.events = EventProviders.newDefaultEventProvider();
    }



    @Override
    public void addPortalListener(PortalListener listener) {
        this.events.addListener(PortalListener.class, listener);
    }



    @Override
    public void removePortalListener(PortalListener listener) {
        this.events.removeListener(PortalListener.class, listener);
    }



    private void fire(List<Portal> portals, Dispatch<PortalListener, PortalEvent> d) {
        this.events
                .dispatchEvent(PortalListener.class, new PortalEvent(this, portals), d);
    }



    @Override
    public synchronized Collection<DBPortal> updatePortals(Sector sector,
            Collection<? extends Portal> portals) throws OrionException {

        final List<Portal> moved = new ArrayList<>();
        final List<Portal> created = new ArrayList<>();
        final List<Portal> removed = new ArrayList<>();
        final List<DBPortal> result = new ArrayList<>(portals.size());
        
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();

            DBSector existingSector = read.findSingle(DBSector.class,
                    DBSector.QUERY_FIND_SECTOR,
                    new Param(sector.getQuadName(), sector.getX(), sector.getY()));

            if (existingSector == null) {
                // if target sector doesn't exist, create it
                final Collection<DBSector> updates = this.quadUpdater
                        .updateSectorInformation(Collections.singleton(sector));
                if (updates.isEmpty()) {
                    // were not able to create sector, so better skip portals too
                    return result;
                }
                existingSector = updates.iterator().next();
            }

            // first step: add new portals
            for (final Portal newPortal : portals) {
                final DBPortal existing = read.findSingle(DBPortal.class,
                        DBPortal.QUERY_PORTAL_BY_TYPE_AND_OWNER,
                        new Param(newPortal.getType(), newPortal.getOwner()));

                if (existing != null && !existing.equals(newPortal)) {
                    // portal exists and differs from new portal
                    // move it to new sector
                    existing.setSector(existingSector);
                    moved.add(existing);
                } else if (existing == null) {
                    // new portal needs to be added
                    final DBPortal portal = new DBPortal(newPortal.getOwner(),
                            newPortal.getOwnerClan(), newPortal.getType(),
                            existingSector, Time.currentTime());
                    write.single(portal);
                    result.add(portal);
                    created.add(portal);
                }

                result.add(existing);
            }

            // second step: remove portals that do not exist anymore
            final Collection<DBPortal> currentPortals = read.findList(DBPortal.class,
                    DBPortal.QUERY_PORTAL_BY_SECTOR, new Param(existingSector));

            for (final DBPortal p : currentPortals) {
                if (!portals.contains(p)) {
                    p.setSector(null);
                    write.remove(p);
                    removed.add(p);
                }
            }


        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
        
        this.fire(created, PortalListener.PORTALS_ADDED);
        this.fire(moved, PortalListener.PORTALS_MOVED);
        this.fire(removed, PortalListener.PORTALS_REMOVED);

        return result;
    }

}
