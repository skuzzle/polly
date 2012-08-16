package polly.rx.entities;


public class BattleReportShip {

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
    
    
    
    
}
