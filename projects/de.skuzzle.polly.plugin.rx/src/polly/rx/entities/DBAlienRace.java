package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.core.orion.model.AlienRace;
import polly.rx.core.orion.model.OrionObjectUtil;

@Entity
@NamedQueries({
    @NamedQuery(
        name = DBAlienRace.FIND_RACE,
        query= "SELECT r FROM DBAlienRace r WHERE r.name = ?1 AND r.subName = ?2 AND r.aggressive = ?3"
    ),
    @NamedQuery(
        name = DBAlienRace.ALL_RACES,
        query = "SELECT r FROM DBAlienRace r"
    )
})
public class DBAlienRace implements AlienRace {
    
    public final static String FIND_RACE = "FIND_RACE"; //$NON-NLS-1$
    public final static String ALL_RACES = "ALL_RACES"; //$NON-NLS-1$
    private final static String GENERATOR = "ALIEN_RACE_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;
    
    private String name;
    private String subName;
    private boolean aggressive;



    public DBAlienRace() {
    }



    public DBAlienRace(String name, String subName, boolean aggressive) {
        Check.objects(name, subName).notNull();
        this.name = name;
        this.subName = subName;
        this.aggressive = aggressive;
    }

    
    
    public void updateWith(AlienRace race) {
        this.name = race.getName();
        this.subName = race.getSubName();
        this.aggressive = race.isAggressive();
    }
    
    
    

    public int getId() {
        return this.id;
    }



    @Override
    public String toString() {
        return OrionObjectUtil.alienRaceString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.alienRaceHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return AlienRace.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.alienRaceEquals(this, (AlienRace) o);
    }



    @Override
    public String getName() {
        return this.name;
    }

    
    
    public void setName(String name) {
        this.name = name;
    }
    
    

    @Override
    public String getSubName() {
        return this.subName;
    }
    
    
    
    
    public void setSubName(String subName) {
        this.subName = subName;
    }



    @Override
    public boolean isAggressive() {
        return this.aggressive;
    }
    
    
    
    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }
}
