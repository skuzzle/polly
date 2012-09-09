package polly.rx.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Transient;


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
    
    @Transient
    private transient BattleDrop[] repairCostOffset;

    @Transient
    private int repairTimeOffset;
    
    
    
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

    
    private final static double CRED_FACTOR_PZ = 6324.0 / 99940.0;
    private final static double NRG_FACTOR_PZ = 6022.0 / 99940.0;
    private final static double ORG_FACTOR_PZ = 9930.0 / 99940.0;
    private final static double FE_FACTOR_PZ = 15031.0 / 99940;
    private final static double LM_FACTOR_PZ = 8428.0 / 99940.0;
    private final static double SM_FACTOR_PZ = 5438.0 / 99940.0;
    private final static double REPAIR_TIME_FACTOR_PZ = 1798920.0 / 99940.0;
    
    
    
    @PostLoad
    void calcCostOffset() {
        this.repairCostOffset = new BattleDrop[7];
        this.repairCostOffset[0] = new BattleDrop(RxRessource.CR, (int)(CRED_FACTOR_PZ * this.pzDamage));
        this.repairCostOffset[1] = new BattleDrop(RxRessource.NRG, (int)(NRG_FACTOR_PZ * this.pzDamage));
        this.repairCostOffset[2] = new BattleDrop(RxRessource.ORG, (int)(ORG_FACTOR_PZ * this.pzDamage));
        this.repairCostOffset[3] = new BattleDrop(RxRessource.SYNTH, 0);
        this.repairCostOffset[4] = new BattleDrop(RxRessource.FE, (int)(FE_FACTOR_PZ * this.pzDamage));
        this.repairCostOffset[5] = new BattleDrop(RxRessource.LM, (int)(LM_FACTOR_PZ * this.pzDamage));
        this.repairCostOffset[6] = new BattleDrop(RxRessource.SM, (int)(SM_FACTOR_PZ * this.pzDamage));
        this.repairTimeOffset = (int) (REPAIR_TIME_FACTOR_PZ * this.pzDamage);
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    public BattleDrop[] getRepairCostOffset() {
        return this.repairCostOffset;
    }
    
    
    
    public int getRepairTimeOffset() {
        return this.repairTimeOffset;
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
