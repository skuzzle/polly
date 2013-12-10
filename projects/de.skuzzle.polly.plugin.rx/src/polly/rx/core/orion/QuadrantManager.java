package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Wormhole;
import polly.rx.entities.DBSector;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.time.Time;


public class QuadrantManager {

    private final PersistenceManagerV2 persistence;
    private final WormholeProvider wormholeProvider;
    private PortalProvider portalProvider;
    
    
    
    public QuadrantManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
        this.wormholeProvider = WormholeProviderFactory.getProvider();
    }
    
    
    
    public List<Wormhole> getWormholes(DBSector sector) {
        return this.wormholeProvider.getWormholesFrom(sector, this.persistence);
    }
    
    
    
    public List<Portal> getPortals(DBSector sector) {
        return this.portalProvider.getPortals(sector);
    }
    
    
    
    public PathPlanner getUniverse() {
        final List<Quadrant> allQuads = new ArrayList<>();
        for (final String quadName : this.getQuadrants()) {
            allQuads.add(this.createQuadrant(quadName));
        }
        return new PathPlanner(allQuads, this);
    }
    
    
    
    public Quadrant createQuadrant(String name) {
        try (final Read read = this.persistence.read()) {
            final List<DBSector> sectors = read.findList(DBSector.class, 
                    DBSector.QUERY_BY_QUADRANT, new Param(name));
            
            final Map<String, Sector> sectMap = new HashMap<>(sectors.size());
            int maxX = 0;
            int maxY = 0;
            for (final DBSector qs : sectors) {
                maxX = Math.max(maxX, qs.getX());
                maxY = Math.max(maxY, qs.getY());
                sectMap.put(qs.getX() + "_" + qs.getY(), qs); //$NON-NLS-1$
            }
            return new Quadrant(name, sectMap, maxX, maxY);
        }
    }
    
    
    
    public Collection<String> getQuadrants() {
        try (final Read read = this.persistence.read()) {
            final List<DBSector> sectors = read.findList(DBSector.class, 
                    DBSector.QUERY_DISTINCT_QUADS);
            
            final Set<String> result = new TreeSet<>();
            for (final DBSector qs : sectors) {
                result.add(qs.getQuadName());
            }
            return result;
        }
    }
    
    
    
    public DBSector getSector(String quadName, int x, int y) {
        return this.persistence.atomic().findSingle(DBSector.class, 
                DBSector.QUERY_FIND_SECTOR, new Param(quadName, x, y));
    }
    
    
    
    public void updateSector(DBSector sector) throws DatabaseException {
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();
            
            final DBSector existing = read.findSingle(DBSector.class, 
                    DBSector.QUERY_FIND_SECTOR, 
                    new Param(sector.getQuadName(), sector.getX(), sector.getY()));
            
            if (existing == null) {
                sector.setDate(Time.currentTime());
                write.all(sector.getRessources());
                write.all(sector.getSpawns());
                write.single(sector);
            } else {
                existing.setDate(Time.currentTime());
                write.removeAll(existing.getRessources());
                write.removeAll(existing.getSpawns());
                
                existing.setSpawns(sector.getSpawns());
                existing.setRessources(sector.getRessources());
                write.all(existing.getRessources());
            }
        }
    }
}
