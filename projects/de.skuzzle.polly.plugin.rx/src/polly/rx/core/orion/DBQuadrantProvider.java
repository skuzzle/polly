package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.QuadrantUtils;
import polly.rx.core.orion.model.Sector;
import polly.rx.entities.DBSector;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.tools.Equatable;


public class DBQuadrantProvider implements QuadrantProvider, QuadrantUpdater {
    
    
    private static class QuadrantImpl implements Quadrant {
        
        private final String name;
        private final Map<String, Sector> sectors;
        private final int maxX;
        private final int maxY;
        
        public QuadrantImpl(String name, Map<String, Sector> sectors, 
                int maxX, int maxY) {
            this.name = name;
            this.sectors = sectors;
            this.maxX = maxX;
            this.maxY = maxY;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public Sector getSector(int x, int y) {
            final String key = QuadrantUtils.createMapKey(x, y);
            Sector qs = this.sectors.get(key);
            if (qs == null) {
                return QuadrantUtils.noneSector(this.name, x, y);
            }
            return qs;
        }
        
        @Override
        public Collection<Sector> getSectors() {
            return this.sectors.values();
        }
        
        @Override
        public int getMaxX() {
            return this.maxX;
        }
        
        @Override
        public int getMaxY() {
            return this.maxY;
        }

        @Override
        public Class<?> getEquivalenceClass() {
            return Quadrant.class;
        }

        @Override
        public boolean actualEquals(Equatable o) {
            return this.name.equals(((Quadrant) o).getName());
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }

    
    
    private final PersistenceManagerV2 persistence;
    private final Map<String, QuadrantImpl> quadCache;
    
    
    
    public DBQuadrantProvider(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
        this.quadCache = new HashMap<>();
    }
    
    
    
    @Override
    public void updateSectorInformation(Sector sector) {
        Sector updated = sector;
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();
            
            DBSector existing = read.findSingle(DBSector.class, 
                    DBSector.QUERY_FIND_SECTOR, 
                    new Param(sector.getQuadName(), sector.getX(), sector.getY()));
            
            if (existing == null) {
                existing = new DBSector(sector);
                write.all(existing.getRessources());
                write.single(existing);
            } else {
                existing.updateFrom(sector, write);
            }
            updated = existing;
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        
        synchronized (this.quadCache) {
            QuadrantImpl quad = this.quadCache.get(sector.getQuadName());
            if (quad != null) {
                quad.sectors.put(QuadrantUtils.createMapKey(sector), updated);
            }
        }
    }
    
    
    
    @Override
    public Quadrant getQuadrant(String name) {
        synchronized (this.quadCache) {
            QuadrantImpl quad = this.quadCache.get(name);
            if (quad == null) {
                try (final Read read = this.persistence.read()) {
                    final Collection<DBSector> sectors = read.findList(DBSector.class, 
                            DBSector.QUERY_BY_QUADRANT, new Param(name));
                    
                    int maxX = 0;
                    int maxY = 0;
                    final Map<String, Sector> sectorMap = new HashMap<>(sectors.size());
                    for (final DBSector sector : sectors) {
                        final String key = QuadrantUtils.createMapKey(sector);
                        sectorMap.put(key, sector);
                        maxX = Math.max(maxX, sector.getX());
                        maxY = Math.max(maxY, sector.getY());
                    }
                    quad = new QuadrantImpl(name, sectorMap, maxX, maxY);
                    this.quadCache.put(name, quad);
                }
            }
            return quad;
        }
    }
    
    
    
    @Override
    public Quadrant getQuadrant(Sector sector) {
        return this.getQuadrant(sector.getQuadName());
    }
    
    
    
    @Override
    public Collection<String> getAllQuadrantNames() {
        try (final Read read = this.persistence.read()) {
            final Collection<DBSector> distinct = read.findList(DBSector.class, 
                    DBSector.QUERY_DISTINCT_QUADS);
            final List<String> result = new ArrayList<>(distinct.size());
            for (final Sector sector : distinct) {
                result.add(sector.getQuadName());
            }
            return result;
        }
    }
    

    
    @Override
    public Collection<Quadrant> getAllQuadrants() {
        final Collection<String> allNames = this.getAllQuadrantNames();
        final List<Quadrant> result = new ArrayList<>(allNames.size());
        for (final String quadName : allNames) {
            result.add(this.getQuadrant(quadName));
        }
        return result;
    }



    @Override
    public Collection<? extends Sector> getEntryPortals() {
        return Collections.emptyList();
    }
}
