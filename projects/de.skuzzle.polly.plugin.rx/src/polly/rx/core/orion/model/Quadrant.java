package polly.rx.core.orion.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import polly.rx.entities.SectorType;


public class Quadrant {
    
    private final String name;
    private final Map<String, Sector> sectors;
    private final Set<String> highlights;
    private final int maxX;
    private final int maxY;

    
    
    public Quadrant(String name, Map<String, Sector> sectors, int maxX, int maxY) {
        this.name = name;
        this.highlights = Collections.emptySet();
        this.sectors = sectors;
        this.maxX = maxX;
        this.maxY = maxY;
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    private String createKey(int x, int y) {
        return x + "_" + y; //$NON-NLS-1$
    }

    
    
    public Sector getSector(int x, int y) {
        final String key = this.createKey(x, y);
        Sector qs = this.sectors.get(key);
        if (qs == null) {
            qs = new Sector();
            qs.setX(x);
            qs.setY(y);
            qs.setType(SectorType.NONE);
        }
        if (this.highlights.contains(key)) {
            qs.setType(SectorType.HIGHLIGHT);
        }
        return qs;
    }
    
    
    
    public Collection<Sector> getSectors() {
        return this.sectors.values();
    }

    
    
    public int getMaxX() {
        return this.maxX;
    }

    
    
    public int getMaxY() {
        return this.maxY;
    }
}
