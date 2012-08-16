package polly.rx.entities;

import java.util.Date;
import java.util.List;


public class BattleReport {

    private int id;
    
    private String quadrant;
    
    private int x;
    
    private int y;
    
    private List<Drop> drop;
    
    private Date date;
    
    private BattleTactic tactic;
    
    private double attackerBonus;
    
    private double defenderBonus;
    
    private double attackerKw;
    
    private double attackerXpMod;
    
    private double defenderKw;
    
    private double defenderXpMod;
    
    private String attackerFleetName;
    
    private String attackerVenadName;
    
    private String defenderFleetName;
    
    private String defenderVenadName;
    
    private List<BattleReportShip> attackerShips;
    
    private List<BattleReportShip> defenderShips;

    
    
    public BattleReport(String quadrant, int x, int y, List<Drop> drop,
        Date date, BattleTactic tactic, double attackerBonus,
        double defenderBonus, double attackerKw, double attackerXpMod,
        double defenderKw, double defenderXpMod, String attackerFleetName,
        String attackerVenadName, String defenderFleetName,
        String defenderVenadName, List<BattleReportShip> attackerShips,
        List<BattleReportShip> defenderShips) {
        super();
        this.quadrant = quadrant;
        this.x = x;
        this.y = y;
        this.drop = drop;
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
        this.attackerShips = attackerShips;
        this.defenderShips = defenderShips;
    }
    
    
    
}