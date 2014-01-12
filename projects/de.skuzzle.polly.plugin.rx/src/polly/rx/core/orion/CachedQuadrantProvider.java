package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;


public class CachedQuadrantProvider extends QuadrantProviderDecorator {

    private final Map<String, Quadrant> quadCache;
    private final List<Sector> entryPortals;
    private final List<Quadrant> allQuadrants;
    
    
    
    public CachedQuadrantProvider(QuadrantProvider wrapped) {
        super(wrapped);
        this.quadCache = new HashMap<>();
        this.allQuadrants = new ArrayList<>();
        this.entryPortals = new ArrayList<>();
    }
    
    
    
    @Override
    public List<? extends Sector> getEntryPortals() {
        if (this.entryPortals.isEmpty()) {
            this.entryPortals.addAll(super.getEntryPortals());
        }
        return this.entryPortals;
    }
    
    
    
    @Override
    public Quadrant getQuadrant(Sector sector) {
        return this.getQuadrant(sector.getQuadName());
    }
    
    
    
    @Override
    public List<? extends Quadrant> getAllQuadrants() {
        if (this.allQuadrants.isEmpty()) {
            this.allQuadrants.addAll(super.getAllQuadrants());
        }
        return this.allQuadrants;
    }

    
    
    @Override
    public Quadrant getQuadrant(String name) {
        Quadrant cached = this.quadCache.get(name);
        if (cached == null) {
            cached = super.getQuadrant(name);
            this.quadCache.put(name, cached);
        }
        return cached;
    }



    @Override
    public void quadrantDeleted(QuadrantEvent e) {
        this.quadCache.remove(e.getQuadrant().getName());
        this.allQuadrants.remove(e.getQuadrant());
        this.entryPortals.removeAll(e.getModified());
    }



    @Override
    public void quadrantAdded(QuadrantEvent e) {
        this.quadCache.put(e.getQuadrant().getName(), e.getQuadrant());
        this.allQuadrants.add(e.getQuadrant());
    }



    @Override
    public void sectorsAdded(QuadrantEvent e) {
        for (final Sector sector : e.getModified()) {
            if (sector.getType() == SectorType.EINTRITTS_PORTAL) {
                this.entryPortals.add(sector);
            }
        }
    }



    @Override
    public void sectorsUpdated(QuadrantEvent e) {}
}
