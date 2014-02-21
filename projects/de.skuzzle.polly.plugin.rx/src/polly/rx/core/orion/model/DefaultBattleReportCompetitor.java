package polly.rx.core.orion.model;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultBattleReportCompetitor implements BattleReportCompetitor {

    private DefaultBattleReport parent;
    private final String ownerName;
    private final String ownerClan;
    private final String fleetName;
    private final float kw;
    private final float xpmod;
    private final List<DefaultReportShip> ships;
    
    
    
    public DefaultBattleReportCompetitor(String ownerName, String ownerClan, 
            String fleetName, float kw, float xpMod, List<? extends ReportShip> ships) {
        Check.objects(ownerName, ownerClan, fleetName, 
                ships).notNull().andCollection(ships).notEmpty();
        this.ownerName = ownerName;
        this.ownerClan = ownerClan;
        this.fleetName = fleetName;
        this.kw = kw;
        this.xpmod = xpMod;
        this.ships = new ArrayList<>(ships.size());
        for (final ReportShip ship : ships) {
            if (!(ship instanceof DefaultReportShip)) {
                this.ships.add(new DefaultReportShip(ship));
            } else {
                this.ships.add((DefaultReportShip) ship);
            }
        }
        
    }

    
    
    public DefaultBattleReportCompetitor(BattleReportCompetitor competitor) {
        this(competitor.getOwnerName(), competitor.getOwnerClan(), 
                competitor.getFleetName(), competitor.getKw(), competitor.getXpMod(), 
                competitor.getShips());
    }


    
    public void setParent(DefaultBattleReport parent) {
        this.parent = parent;
    }



    @Override
    public String getOwnerName() {
        return this.ownerName;
    }



    @Override
    public String getOwnerClan() {
        return this.ownerClan;
    }



    @Override
    public boolean isWinner() {
        return this.parent != null && this.parent.getWinner() == this;
    }



    @Override
    public String getFleetName() {
        return this.fleetName;
    }



    @Override
    public float getKw() {
        return this.kw;
    }



    @Override
    public float getXpMod() {
        return this.xpmod;
    }



    @Override
    public List<DefaultReportShip> getShips() {
        return this.ships;
    }

    
    
    @Override
    public int hashCode() {
        return OrionObjectUtil.competitorHash(this);
    }
    
    
    
    @Override
    public String toString() {
        return OrionObjectUtil.competitorString(this);
    }


    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return BattleReportCompetitor.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.competitorsEqual(this, (BattleReportCompetitor) o);
    }
}
