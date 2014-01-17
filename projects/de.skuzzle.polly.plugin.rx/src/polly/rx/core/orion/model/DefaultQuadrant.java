package polly.rx.core.orion.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import polly.rx.core.orion.QuadrantUtils;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultQuadrant implements Quadrant {

    private final Map<String, DefaultSector> sectors;
    private final String name;
    private final int maxX;
    private final int maxY;



    public DefaultQuadrant(String name, int maxX, int maxY,
            Map<String, ? extends Sector> sectorMap) {
        this(name, maxX, maxY, sectorMap.values());
    }



    public DefaultQuadrant(String name, int maxX, int maxY,
            Collection<? extends Sector> sectors) {
        Check.notNull(name, sectors);
        this.name = name;
        this.maxX = maxX;
        this.maxY = maxY;
        final Map<String, DefaultSector> m = new HashMap<>(sectors.size());
        for (final Sector s : sectors) {
            m.put(QuadrantUtils.createMapKey(s), new DefaultSector(s));
        }
        this.sectors = Collections.unmodifiableMap(m);
    }



    public DefaultQuadrant(Quadrant q) {
        this(q.getName(), q.getMaxX(), q.getMaxY(), q.getSectors());
    }



    @Override
    public String toString() {
        return OrionObjectUtil.quadrantString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.quadrantHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Quadrant.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.quadrantsEquals(this, (Quadrant) o);
    }



    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public Sector getSector(int x, int y) {
        final Sector s = this.sectors.get(QuadrantUtils.createMapKey(x, y));
        if (s == null) {
            return new DefaultSector(this.name, x, y, 0, 0, 0, SectorType.NONE,
                    Collections.<Production> emptyList());
        }
        return s;
    }



    @Override
    public Collection<? extends Sector> getSectors() {
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
}
