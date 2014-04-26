package polly.annoyingPeople.core;

import polly.annoyingPeople.entities.AnnoyingPerson;
import de.skuzzle.jeve.Event;


public class AnnoyingPersonEvent extends Event<PersonManager> {
    
    private final AnnoyingPerson person;

    public AnnoyingPersonEvent(PersonManager source, AnnoyingPerson person) {
        super(source);
        this.person = person;
    }

    
    public AnnoyingPerson getPerson() {
        return this.person;
    }
}
