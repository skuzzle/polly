package polly.rx.core.orion.model;

import java.util.Date;

import polly.rx.entities.ShipType;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultReportShip implements ReportShip {

    private final ShipType type;
    private final String capiName;
    private final String shipName;
    private final String ownerName;
    private final String ownerClan;
    private final DefaultShipStats stats;
    private final DefaultShipStats damage;
    private final int shipClass;
    private final int rxId;
    private final Date date;
    private final int crewXp;
    private final int capiXp;



    public DefaultReportShip(final ShipType type, String shipName, String ownerName,
            String ownerClan, String capiName, DefaultShipStats stats,
            DefaultShipStats damage, int shipClass, int rxId, int crewXp, int capiXp,
            Date date) {

        Check.objects(type, shipName, ownerName, ownerClan, capiName, stats, damage, date)
                .notNull();
        this.type = type;
        this.shipName = shipName;
        this.ownerName = ownerName;
        this.ownerClan = ownerClan;
        this.capiName = capiName;
        this.stats = stats;
        this.damage = damage;
        this.shipClass = shipClass;
        this.rxId = rxId;
        this.crewXp = crewXp;
        this.capiXp = capiXp;
        this.date = date;
    }



    public DefaultReportShip(ReportShip ship) {
        this(ship.getType(), ship.getShipName(), ship.getOwnerName(),
                ship.getOwnerClan(), ship.getCapiName(), new DefaultShipStats(
                        ship.getStats()), new DefaultShipStats(ship.getDamage()), 
                        ship.getShipClass(), ship.getRxId(), ship.getCrewXp(), 
                        ship.getCapiXp(), new Date(ship.getDate().getTime()));
    }



    @Override
    public int getCapiXp() {
        return this.capiXp;
    }



    @Override
    public int getCrewXp() {
        return this.crewXp;
    }



    @Override
    public String getCapiName() {
        return this.capiName;
    }



    @Override
    public ShipType getType() {
        return this.type;
    }



    @Override
    public String getShipName() {
        return this.shipName;
    }



    @Override
    public int getShipClass() {
        return this.shipClass;
    }



    @Override
    public int getRxId() {
        return this.rxId;
    }



    @Override
    public Date getDate() {
        return this.date;
    }



    @Override
    public String toString() {
        return super.toString();
    }



    @Override
    public int hashCode() {
        return super.hashCode();
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return ReportShip.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return false;
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
    public ShipStats getStats() {
        return this.stats;
    }



    @Override
    public ShipStats getDamage() {
        return this.damage;
    }
}
