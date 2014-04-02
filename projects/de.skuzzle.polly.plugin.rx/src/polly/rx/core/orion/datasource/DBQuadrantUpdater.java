package polly.rx.core.orion.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.QuadrantEvent;
import polly.rx.core.orion.QuadrantListener;
import polly.rx.core.orion.QuadrantUpdater;
import polly.rx.core.orion.QuadrantUtils;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.entities.DBQuadrant;
import polly.rx.entities.DBSector;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.EventProviders;

public class DBQuadrantUpdater implements QuadrantUpdater {

    private final PersistenceManagerV2 persistence;
    private final EventProvider eventProvider;
    


    public DBQuadrantUpdater(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
        this.eventProvider = EventProviders.newDefaultEventProvider();
    }

    
    
    public void addQuadrantListener(QuadrantListener listener) {
        this.eventProvider.addListener(QuadrantListener.class, listener);
    }
    
    
    
    public void removeQuadrantListener(QuadrantListener listener) {
        this.eventProvider.removeListener(QuadrantListener.class, listener);
    }

    
    
    private void fireQuadrantDeleted(Quadrant quad) {
        final QuadrantEvent e = new QuadrantEvent(this, quad, quad.getSectors());
        this.eventProvider.dispatch(QuadrantListener.class, e, 
                QuadrantListener::quadrantDeleted);
    }
    
    
    
    private void fireQuadrantAdded(Quadrant quad) {
        final QuadrantEvent e = new QuadrantEvent(this, quad, quad.getSectors());
        this.eventProvider.dispatch(QuadrantListener.class, e, 
                QuadrantListener::quadrantAdded);
    }
    
    
    
    private void fireSectorsAdded(Collection<? extends Sector> sectors) {
        final QuadrantEvent e = new QuadrantEvent(this, null, sectors);
        this.eventProvider.dispatch(QuadrantListener.class, e, 
                QuadrantListener::sectorsAdded);
    }
    
    
    
    private void fireSectorsUpdated(Collection<? extends Sector> sectors) {
        final QuadrantEvent e = new QuadrantEvent(this, null, sectors);
        this.eventProvider.dispatch(QuadrantListener.class, e, 
                QuadrantListener::sectorsUpdated);
    }
    
    

    @Override
    public void deleteQuadrant(String quadName) throws OrionException {
        quadName = quadName.trim();
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();
            final DBQuadrant existing = read.findSingle(DBQuadrant.class, 
                    DBQuadrant.QUERY_QUADRANT_BY_NAME, new Param(quadName));
            
            if (existing != null) {
                write.removeAll(existing.getSectors());
                write.remove(existing);
                this.fireQuadrantDeleted(existing);
                
            }
            
        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
    }



    @Override
    public void deleteQuadrant(Quadrant quadrant) throws OrionException {
        this.deleteQuadrant(quadrant.getName());
    }



    @Override
    public synchronized Collection<DBSector> updateSectorInformation(
            Collection<? extends Sector> sectors) throws OrionException {
        
        if (sectors.isEmpty()) {
            return Collections.emptyList();
        }
        
        final Map<String, DBQuadrant> quadCache = new HashMap<>();
        final Map<String, DBSector> sectorCache = new HashMap<>();
        final Collection<DBSector> updated = new HashSet<>();
        
        
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();
            
            for (final Sector sector : sectors) {
                final DBSector existing = this.findSector(read, sectorCache, sector);
                
                if (existing != null) {
                    existing.updateFrom(sector, write);
                    updated.add(existing);
                } else {
                    DBQuadrant quadrant = this.findQuadrant(read, quadCache, 
                            sector.getQuadName());
                    
                    if (quadrant == null) {
                        // create new quadrant
                        quadrant = new DBQuadrant(sector.getQuadName());
                        quadCache.put(sector.getQuadName(), quadrant);
                        write.single(quadrant);
                    }
                    final DBSector newSector = quadrant.updateFrom(sector, write);
                    if (newSector != null) {
                        sectorCache.put(QuadrantUtils.createMapKey(newSector), newSector);
                    }
                }
            }
        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
        
        for (final DBQuadrant quad : quadCache.values()) {
            this.fireQuadrantAdded(quad);
        }
        this.fireSectorsAdded(sectorCache.values());
        this.fireSectorsUpdated(updated);
        final ArrayList<DBSector> result = new ArrayList<>(
                sectorCache.values().size() + updated.size());
        result.addAll(sectorCache.values());
        result.addAll(updated);
        return result;
    }
    
    
    
    private final DBSector findSector(Read read, Map<String, DBSector> tempCache, Sector s) {
        DBSector sector = tempCache.get(QuadrantUtils.createMapKey(s));
        if (sector == null) {
            sector = read.findSingle(DBSector.class, 
                    DBSector.QUERY_FIND_SECTOR, 
                    new Param(s.getQuadName(), s.getX(), s.getY()));
        }
        return sector;
    }
    
    
    
    private DBQuadrant findQuadrant(Read read, Map<String, DBQuadrant> tempCache, 
            String name) {
        DBQuadrant quad = tempCache.get(name);
        if (quad == null) {
            quad = read.findSingle(DBQuadrant.class, 
                    DBQuadrant.QUERY_QUADRANT_BY_NAME, 
                    new Param(name)); 
        }
        return quad;
    }
}
