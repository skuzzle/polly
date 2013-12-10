package polly.rx.core.orion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.entities.QuadSector;
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
    
    
    
    public List<Wormhole> getWormholes(QuadSector sector) {
        return this.wormholeProvider.getWormholesFrom(sector, this.persistence);
    }
    
    
    
    public List<Portal> getPortals(QuadSector sector) {
        return this.portalProvider.getPortals(sector);
    }
    
    
    
    public Quadrant createQuadrant(String name) {
        try (final Read read = this.persistence.read()) {
            final List<QuadSector> sectors = read.findList(QuadSector.class, 
                    QuadSector.QUERY_BY_QUADRANT, new Param(name));
            
            final Map<String, QuadSector> sectMap = new HashMap<>(sectors.size());
            int maxX = 0;
            int maxY = 0;
            for (final QuadSector qs : sectors) {
                maxX = Math.max(maxX, qs.getX());
                maxY = Math.max(maxY, qs.getY());
                sectMap.put(qs.getX() + "_" + qs.getY(), qs); //$NON-NLS-1$
            }
            return new Quadrant(name, sectMap, maxX, maxY);
        }
    }
    
    
    
    public QuadSector getSector(String quadName, int x, int y) {
        return this.persistence.atomic().findSingle(QuadSector.class, 
                QuadSector.QUERY_FIND_SECTOR, new Param(quadName, x, y));
    }
    
    
    
    public void updateSector(QuadSector sector) throws DatabaseException {
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();
            
            final QuadSector existing = read.findSingle(QuadSector.class, 
                    QuadSector.QUERY_FIND_SECTOR, 
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
