package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class BattleReportShip {

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private int rxId;
    
    private String name;
    
    private String capi;
    
    private int attack;
    
    private int shields;
    
    private int pz;
    
    private int structure;
    
    private int minCrew;
    
    private int maxCrew;
    
    private int systems;
    
    private int capiXp;
    
    private int crewXp;
    
    private int awDamage;
    
    private int capiHp;
    
    private int hpDamage;
    
    private int shieldDamage;
    
    private int pzDamage;
    
    private int structureDamage;
    
    private int systemsDamage;
    
    private int crewDamage;
    
    
    public BattleReportShip() {
        this(0, "", "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    
    

    public BattleReportShip(int rxId, String name, String capi, int attack,
        int shields, int pz, int structure, int minCrew, int maxCrew,
        int systems, int capiXp, int crewXp, int shieldDamage, int pzDamage,
        int structureDamage, int systemsDamage, int hp, int hpDamage, int awDamage, 
        int crewDamage) {
        
        super();
        this.rxId = rxId;
        this.name = name;
        this.capi = capi;
        this.attack = attack;
        this.shields = shields;
        this.pz = pz;
        this.structure = structure;
        this.minCrew = minCrew;
        this.maxCrew = maxCrew;
        this.systems = systems;
        this.capiXp = capiXp;
        this.crewXp = crewXp;
        this.shieldDamage = shieldDamage;
        this.pzDamage = pzDamage;
        this.structureDamage = structureDamage;
        this.systemsDamage = systemsDamage;
        this.capiHp = hp;
        this.hpDamage = hpDamage;
        this.awDamage = awDamage;
        this.crewDamage = crewDamage;
    }

    
    
    public int getId() {
        return this.id;
    }

    
    
    public int getRxId() {
        return this.rxId;
    }

    
    
    public String getName() {
        return this.name;
    }

    
    
    public String getCapi() {
        return this.capi;
    }

    
    
    public int getAttack() {
        return this.attack;
    }

    
    
    public int getShields() {
        return this.shields;
    }

    
    
    public int getPz() {
        return this.pz;
    }

    
    
    public int getStructure() {
        return this.structure;
    }

    
    
    public int getMinCrew() {
        return this.minCrew;
    }

    
    
    public int getMaxCrew() {
        return this.maxCrew;
    }

    
    
    public int getSystems() {
        return this.systems;
    }

    
    
    public int getCapiXp() {
        return this.capiXp;
    }

    
    
    public int getCrewXp() {
        return this.crewXp;
    }

    
    
    public int getAwDamage() {
        return this.awDamage;
    }

    
    
    public int getCapiHp() {
        return this.capiHp;
    }

    
    
    public int getHpDamage() {
        return this.hpDamage;
    }

    
    
    public int getShieldDamage() {
        return this.shieldDamage;
    }

    
    
    public int getPzDamage() {
        return this.pzDamage;
    }

    
    
    public int getStructureDamage() {
        return this.structureDamage;
    }

    
    
    public int getSystemsDamage() {
        return this.systemsDamage;
    }

    
    
    public int getCrewDamage() {
        return this.crewDamage;
    }
    
    
    
    public int calcMaxWend() {
        return this.systems - this.shields;
    }
    
    
    
    @Override
    public String toString() {
        return this.name;
    }
}
