package polly.rx.core.orion.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultSector implements Sector {

    private final String quadName;
    private final int x;
    private final int y;
    private final int attacker;
    private final int defender;
    private final int guard;
    private final SectorType type;
    private final Date date;
    private final List<? extends Production> production;


    
    public DefaultSector(String quadName, int x, int y, int attacker, int defender,
            int guard, SectorType type, Collection<? extends Production> production) {
        Check.objects(quadName, production).notNull();
        this.quadName = quadName;
        this.x = x;
        this.y = y;
        this.attacker = attacker;
        this.defender = defender;
        this.guard = guard;
        this.type = type;
        this.date = Time.currentTime();
        this.production = Collections.unmodifiableList(new ArrayList<>(production));
    }

    
    
    public DefaultSector(Sector s) {
        this(s.getQuadName(),
                s.getX(),
                s.getY(),
                s.getAttackerBonus(),
                s.getDefenderBonus(),
                s.getSectorGuardBonus(),
                s.getType(),
                s.getRessources());
    }
    


    @Override
    public String toString() {
        return OrionObjectUtil.sectorString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.sectorHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Sector.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.sectorsEqual(this, (Sector) o);
    }



    @Override
    public String getQuadName() {
        return this.quadName;
    }



    @Override
    public int getX() {
        return this.x;
    }



    @Override
    public int getY() {
        return this.y;
    }



    @Override
    public int getAttackerBonus() {
        return this.attacker;
    }



    @Override
    public int getDefenderBonus() {
        return defender;
    }



    @Override
    public int getSectorGuardBonus() {
        return this.guard;
    }



    @Override
    public Date getDate() {
        return this.date;
    }



    @Override
    public SectorType getType() {
        return this.type;
    }



    @Override
    public Collection<? extends Production> getRessources() {
        return this.production;
    }
    
    
    
    @Override
    public int compareTo(Sector o) {
        return SECTOR_COMPERATOR.compare(this, o);
    }
}
