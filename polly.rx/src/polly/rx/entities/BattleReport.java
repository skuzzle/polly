package polly.rx.entities;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import polly.rx.core.SumQuery;


@Entity
@NamedQueries({
    @NamedQuery(
        name = "ALL_REPORTS",
        query= "SELECT rp FROM BattleReport rp"
    ),
    @NamedQuery(
        name = "UNIQUE_CHECK",
        query = "SELECT rp FROM BattleReport rp WHERE " + 
            "rp.quadrant = ?1 AND " + 
            "rp.x = ?2 AND " + 
            "rp.y = ?3 AND " +
            "rp.attackerVenadName = ?4 AND " +
            "rp.defenderVenadName = ?5 AND " +
            "rp.attackerFleetName = ?6 AND " +
            "rp.defenderFleetName = ?7 AND " +
            "rp.date = ?8"
    ),
    @NamedQuery(
        name = "WITH_NAME",
        query = "SELECT rp FROM BattleReport rp WHERE rp.attackerVenadName = ?1 OR " +
            "rp.defenderVenadName = ?1"
    ),
    @NamedQuery(
        name = "WITH_CLAN",
        query = "SELECT rp FROM BattleReport rp WHERE rp.attackerClan = ?1 OR " +
            "rp.defenderClan = ?1"
    ),
    @NamedQuery(
        name = "BY_LOCATION",
        query = "SELECT rp FROM BattleReport rp WHERE rp.quadrant = ?1"
    ),
    @NamedQuery(
        name = "BY_USER_ID",
        query = "SELECT rp FROM BattleReport rp WHERE rp.submitterId = ?1"
    )
})
public class BattleReport {

    public static final String ALL_REPORTS = "ALL_REPORTS";
    public static final String UNIQUE_CHECK = "UNIQUE_CHECK";
    public static final String WITH_NAME = "WITH_NAME";
    public static final String WITH_CLAN = "WITH_CLAN";
    public static final String BY_LOCATION = "BY_LOCATION";
    public static final String BY_USER_ID = "BY_USER_ID";
    
    
    
    public final static BattleReport switchAttacker(BattleReport report) {
        return new BattleReport(
            report.submitterId, report.quadrant, report.x, report.y, 
            report.battleDrops, report.artifact, report.date, report.tactic, 
            report.defenderBonus, report.attackerBonus, report.defenderKw, 
            report.defenderXpMod, report.attackerKw, report.attackerXpMod, 
            report.defenderFleetName, report.defenderVenadName, 
            report.attackerFleetName, report.attackerVenadName, 
            report.defenderClan, report.attackerClan, report.defenderShips, 
            report.attackerShips);
    }
    
    
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private int submitterId;
    
    private String quadrant;
    
    private int x;
    
    private int y;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<BattleDrop> battleDrops;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;
    
    @Enumerated(EnumType.ORDINAL)
    private BattleTactic tactic;
    
    private boolean artifact;
    
    private double attackerBonus;
    
    private double defenderBonus;
    
    private double attackerKw;
    
    private double attackerXpMod;
    
    private double defenderKw;
    
    private double defenderXpMod;
    
    private String attackerFleetName;
    
    private String attackerVenadName;
    
    private String attackerClan;
    
    private String defenderFleetName;
    
    private String defenderVenadName;
    
    private String defenderClan;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BATTLE_REPORT_ATTACKERS")
    private List<BattleReportShip> attackerShips;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BATTLE_REPORT_DEFENDERS")
    private List<BattleReportShip> defenderShips;
    
    
    
    public BattleReport() {
        this(0, "", 0, 0, new LinkedList<BattleDrop>(), false, new Date(), 
            BattleTactic.NORMAL, 
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "", "", "", "", "", "",
            new LinkedList<BattleReportShip>(), new LinkedList<BattleReportShip>());
    }
    
    
    
    public BattleReport(int submitterId, String quadrant, int x, int y, List<BattleDrop> battleDrop,
        boolean artifact,
        Date date, BattleTactic tactic, double attackerBonus,
        double defenderBonus, double attackerKw, double attackerXpMod,
        double defenderKw, double defenderXpMod, String attackerFleetName,
        String attackerVenadName, String defenderFleetName,
        String defenderVenadName, String attackerClan, String defenderClan, 
        List<BattleReportShip> attackerShips,
        List<BattleReportShip> defenderShips) {
        super();
        this.submitterId = submitterId;
        this.quadrant = quadrant;
        this.x = x;
        this.y = y;
        this.battleDrops = battleDrop;
        this.artifact = artifact;
        this.date = date;
        this.tactic = tactic;
        this.attackerBonus = attackerBonus;
        this.defenderBonus = defenderBonus;
        this.attackerKw = attackerKw;
        this.attackerXpMod = attackerXpMod;
        this.defenderKw = defenderKw;
        this.defenderXpMod = defenderXpMod;
        this.attackerFleetName = attackerFleetName;
        this.attackerVenadName = attackerVenadName;
        this.defenderFleetName = defenderFleetName;
        this.defenderVenadName = defenderVenadName;
        this.attackerClan = attackerClan;
        this.defenderClan = defenderClan;
        this.attackerShips = attackerShips;
        this.defenderShips = defenderShips;
    }


    
    public int getId() {
        return this.id;
    }
    
    
    
    public int getSubmitterId() {
        return this.submitterId;
    }


    
    public String getQuadrant() {
        return this.quadrant;
    }


    
    public int getX() {
        return this.x;
    }


    
    public int getY() {
        return this.y;
    }


    
    public List<BattleDrop> getDrop() {
        return this.battleDrops;
    }
    
    
    
    public boolean hasArtifact() {
        return this.artifact;
    }


    
    public Date getDate() {
        return this.date;
    }


    
    public BattleTactic getTactic() {
        return this.tactic;
    }


    
    public double getAttackerBonus() {
        return this.attackerBonus;
    }


    
    public double getDefenderBonus() {
        return this.defenderBonus;
    }


    
    public double getAttackerKw() {
        return this.attackerKw;
    }


    
    public double getAttackerXpMod() {
        return this.attackerXpMod;
    }


    
    public double getDefenderKw() {
        return this.defenderKw;
    }


    
    public double getDefenderXpMod() {
        return this.defenderXpMod;
    }


    
    public String getAttackerFleetName() {
        return this.attackerFleetName;
    }


    
    public String getAttackerVenadName() {
        return this.attackerVenadName;
    }


    
    public String getDefenderFleetName() {
        return this.defenderFleetName;
    }


    
    public String getDefenderVenadName() {
        return this.defenderVenadName;
    }
    
    
    
    public String getAttackerClan() {
        return this.attackerClan;
    }
    
    
    
    public String getDefenderClan() {
        return this.defenderClan;
    }


    
    public List<BattleReportShip> getAttackerShips() {
        return this.attackerShips;
    }


    
    public List<BattleReportShip> getDefenderShips() {
        return this.defenderShips;
    }
    
    
    
    public int querySumAttacker(SumQuery query) {
        return this.querySum(query, this.attackerShips);
    }
    
    
    
    public int querySumDefender(SumQuery query) {
        return this.querySum(query, this.defenderShips);
    }
    
    
    
    private int querySum(SumQuery query, List<BattleReportShip> ships) {
        int sum = 0;
        for (BattleReportShip ship : ships) {
            sum += query.getValue(ship);
        }
        return sum;
    }
}