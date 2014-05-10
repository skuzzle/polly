package polly.rx.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.core.orion.model.OrionChatEntry;
import polly.rx.core.orion.model.OrionObjectUtil;

@Entity
@NamedQuery(
    name = DBOrionChatEntry.YOUNGEST_ENTRIES,
    query = "SELECT oce FROM DBOrionChatEntry oce ORDER BY oce.id DESC"
)
public class DBOrionChatEntry implements OrionChatEntry {

    public final static String YOUNGEST_ENTRIES = "OCE_YOUNGEST_ENTRIES"; //$NON-NLS-1$
    private final static String GENERATOR = "CHAT_ENTRY_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String sender;
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;



    public DBOrionChatEntry() {
    }



    public DBOrionChatEntry(OrionChatEntry oce) {
        Check.objects(oce.getSender(), oce.getMessage(), oce.getDate()).notNull();
        this.sender = oce.getSender();
        this.message = oce.getMessage();
        this.date = oce.getDate();
    }
    
    
    
    public int getId() {
        return this.id;
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return OrionChatEntry.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.chatEntriesEqual(this, (OrionChatEntry) o);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.chatEntryHashCode(this);
    }



    @Override
    public String toString() {
        return OrionObjectUtil.chatEntryString(this);
    }



    @Override
    public Date getDate() {
        return this.date;
    }



    @Override
    public String getSender() {
        return this.sender;
    }



    @Override
    public String getMessage() {
        return this.message;
    }

}
