package polly.rx.core.orion.model;

import java.util.Collection;
import java.util.Date;


public class Sector {
    private String quadName;
    private int x;
    private int y;
    private int attackerBonus;
    private int defenderBonus;
    private int sectorGuardBonus;
    private Date date;
    private SectorType type;
    private Collection<Production> ressources;
    private Collection<Spawn> spawns;
    
    public String getQuadName() {
        return this.quadName;
    }
    
    public void setQuadName(String quadName) {
        this.quadName = quadName;
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getAttackerBonus() {
        return this.attackerBonus;
    }
    
    public void setAttackerBonus(int attackerBonus) {
        this.attackerBonus = attackerBonus;
    }
    
    public int getDefenderBonus() {
        return this.defenderBonus;
    }
    
    public void setDefenderBonus(int defenderBonus) {
        this.defenderBonus = defenderBonus;
    }
    
    public int getSectorGuardBonus() {
        return this.sectorGuardBonus;
    }
    
    public void setSectorGuardBonus(int sectorGuardBonus) {
        this.sectorGuardBonus = sectorGuardBonus;
    }
    
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public SectorType getType() {
        return this.type;
    }
    
    public void setType(SectorType type) {
        this.type = type;
    }
    
    public Collection<Production> getRessources() {
        return this.ressources;
    }
    
    public void setRessources(Collection<Production> ressources) {
        this.ressources = ressources;
    }
    
    public Collection<Spawn> getSpawns() {
        return this.spawns;
    }
    
    public void setSpawns(Collection<Spawn> spawns) {
        this.spawns = spawns;
    }
}
