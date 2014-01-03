package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class DBSpawn {

    private final static String GENERATOR = "SPAWN_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String name;



    public DBSpawn() {
    }



    public String getName() {
        return this.name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public int getId() {
        return this.id;
    }
}
