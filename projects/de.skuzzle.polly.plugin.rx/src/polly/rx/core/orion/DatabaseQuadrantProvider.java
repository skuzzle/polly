package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Spawn;
import polly.rx.entities.DBProduction;
import polly.rx.entities.DBSector;
import polly.rx.entities.DBSpawn;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;


public class DatabaseQuadrantProvider implements QuadrantProvider {

    public static String createKey(int x, int y) {
        return x + "_" + y; //$NON-NLS-1$
    }
    
    
    private final PersistenceManagerV2 persistence;
    
    
    public DatabaseQuadrantProvider(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }
    
    
    
    @Override
    public Collection<String> getAllQuadrantNames() {
        try (final Read read = this.persistence.read()) {
            final List<DBSector> distinct = read.findList(DBSector.class, 
                    DBSector.QUERY_DISTINCT_QUADS);
            final Set<String> result = new TreeSet<>();
            for (final DBSector s : distinct) {
                result.add(s.getQuadName());
            }
            return result;
        }
    }

    
    
    
    private Sector fromDBSector(DBSector s) {
        final Sector result = new Sector();
        result.setQuadName(s.getQuadName());
        result.setX(s.getX());
        result.setY(s.getY());
        result.setAttackerBonus(s.getAttackerBonus());
        result.setDefenderBonus(s.getDefenderBonus());
        result.setSectorGuardBonus(s.getSectorGuardBonus());
        result.setType(s.getType());
        result.setDate(s.getDate());
        result.setRessources(new ArrayList<Production>());
        result.setSpawns(new ArrayList<Spawn>());
        
        for (final DBProduction prod : s.getRessources()) {
            result.getRessources().add(new Production(prod.getRess(), prod.getRate()));
        }
        for (final DBSpawn spawn : s.getSpawns()) {
            result.getSpawns().add(new Spawn(spawn.getName()));
        }
        return result;
    }
    
    
    
    @Override
    public Quadrant getQuadrant(Sector sector) {
        return this.getQuadrant(sector.getQuadName());
    }
    
    
    
    @Override
    public Quadrant getQuadrant(String name) {
        try (final Read read = this.persistence.read()) {
            final List<DBSector> sectors = read.findList(DBSector.class, 
                    DBSector.QUERY_BY_QUADRANT, new Param(name));
            
            final Map<String, Sector> sectorMap = new HashMap<>();
            int maxX = 0;
            int maxY = 0;
            for (final DBSector s : sectors) {
                maxX = Math.max(maxX, s.getX());
                maxY = Math.max(maxY, s.getY());
                sectorMap.put(createKey(s.getX(), s.getY()), this.fromDBSector(s));
            }
            return new Quadrant(name, sectorMap, maxX, maxY);
        }
    }
    
    
    
    @Override
    public Collection<Quadrant> getAllQuadrants() {
        final List<Quadrant> result = new ArrayList<>();
        for (final String quadName : this.getAllQuadrantNames()) {
            result.add(this.getQuadrant(quadName));
        }
        return result;
    }
}
