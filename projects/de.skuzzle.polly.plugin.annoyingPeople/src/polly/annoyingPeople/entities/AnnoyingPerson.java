package polly.annoyingPeople.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

@Entity
@NamedQueries({
    @NamedQuery(
        name = AnnoyingPerson.PERSON_BY_NAME_AND_CHANNEL,
        query= "SELECT ap FROM AnnoyingPerson ap WHERE LOWER(ap.name) = LOWER(?1) AND LOWER(ap.channel) = LOWER(?2)"
    )
})
public class AnnoyingPerson {

    public final static String PERSON_BY_NAME_AND_CHANNEL = "PERSON_BY_NAME_AND_CHANNEL"; //$NON-NLS-1$
    private final static String GENERATOR = "ANNOYING_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String name;
    private String channel;



    public AnnoyingPerson() {
    }



    public AnnoyingPerson(String name, String channel) {
        this.name = name;
        this.channel = channel;
    }



    public final int getId() {
        return this.id;
    }



    public final String getName() {
        return this.name;
    }



    public final String getChannel() {
        return this.channel;
    }
}
