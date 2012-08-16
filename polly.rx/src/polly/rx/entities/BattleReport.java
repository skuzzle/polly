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
}