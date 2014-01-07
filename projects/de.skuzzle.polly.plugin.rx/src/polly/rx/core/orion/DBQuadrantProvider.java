package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.entities.DBQuadrant;
import polly.rx.entities.DBSector;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;


public class DBQuadrantProvider implements QuadrantProvider {
       
    private final PersistenceManagerV2 persistence;
    
    
    public DBQuadrantProvider(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }



    @Override
    public Collection<String> getAllQuadrantNames() {
        final Collection<DBQuadrant> quads = this.getAllQuadrants();
        final List<String> names = new ArrayList<>(quads.size());
        for (final DBQuadrant quad : quads) {
            names.add(quad.getName());
        }
        return names;
    }



    @Override
    public Collection<DBSector> getEntryPortals() {
            return this.persistence.atomic().findList(DBSector.class, 
                    DBSector.QUERY_SECTOR_BY_TYPE, new Param(SectorType.EINTRITTS_PORTAL));
    }



    @Override
    public DBQuadrant getQuadrant(Sector sector) {
        return this.getQuadrant(sector.getQuadName());
    }



    @Override
    public DBQuadrant getQuadrant(String name) {
        name = name.trim();
        DBQuadrant quad = this.persistence.atomic().findSingle(DBQuadrant.class, 
                DBQuadrant.QUERY_QUADRANT_BY_NAME, new Param(name));
        
        if (quad == null) {
            quad = new DBQuadrant(name);
        }
        return quad;
    }



    @Override
    public Collection<DBQuadrant> getAllQuadrants() {
        return this.persistence.atomic().findList(DBQuadrant.class, 
                DBQuadrant.QUERY_ALL_QUADRANTS);
    }



    @Override
    public void quadrantDeleted(QuadrantEvent e) {}



    @Override
    public void quadrantAdded(QuadrantEvent e) {}



    @Override
    public void sectorsAdded(QuadrantEvent e) {}



    @Override
    public void sectorsUpdated(QuadrantEvent e) {}
}
