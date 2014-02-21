package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.entities.BattleTactic;

public class DefaultBattleReport implements BattleReport {

    private final BattleTactic tactic;
    private final DefaultBattleReportCompetitor attacker;
    private final DefaultBattleReportCompetitor defender;
    private final DefaultSector sector;
    private final DefaultDrop drop;
    private final Date date;



    public DefaultBattleReport(BattleTactic tactic,
            DefaultBattleReportCompetitor attacker,
            DefaultBattleReportCompetitor defender, DefaultSector sector,
            DefaultDrop drop, Date date) {
        Check.objects(tactic, attacker, defender, sector, drop, date).notNull();
        this.tactic = tactic;
        this.attacker = attacker;
        this.defender = defender;
        this.sector = sector;
        this.drop = drop;
        this.date = date;

        attacker.setParent(this);
        defender.setParent(this);
    }



    public DefaultBattleReport(BattleReport report) {
        this(report.getTactic(), new DefaultBattleReportCompetitor(report.getAttacker()),
                new DefaultBattleReportCompetitor(report.getDefender()),
                new DefaultSector(report.getSector()), new DefaultDrop(report.getDrop()),
                new Date(report.getDate().getTime()));
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.reportHash(this);
    }



    @Override
    public String toString() {
        return OrionObjectUtil.reportString(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return BattleReport.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.reportsEqual(this, (BattleReport) o);
    }



    @Override
    public Date getDate() {
        return this.date;
    }



    @Override
    public BattleTactic getTactic() {
        return this.tactic;
    }



    @Override
    public Sector getSector() {
        return this.sector;
    }



    @Override
    public Drop getDrop() {
        return this.drop;
    }



    @Override
    public BattleReportCompetitor getWinner() {
        return this.attacker.getKw() * this.sector.getAttackerBonus() >= this.defender.getKw() * this.sector.getDefenderBonus() 
                ? this.attacker : this.defender;
    }



    @Override
    public BattleReportCompetitor getLoser() {
        return this.attacker.getKw() * this.sector.getAttackerBonus() >= this.defender.getKw() * this.sector.getDefenderBonus() 
                ? this.defender : this.attacker;
    }



    @Override
    public BattleReportCompetitor getAttacker() {
        return this.attacker;
    }



    @Override
    public BattleReportCompetitor getDefender() {
        return this.defender;
    }
}
