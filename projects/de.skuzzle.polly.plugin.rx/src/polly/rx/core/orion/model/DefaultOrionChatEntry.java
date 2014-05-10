package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.Equatable;

public class DefaultOrionChatEntry implements OrionChatEntry {

    private final String sender;
    private final String message;
    private final Date date;



    public DefaultOrionChatEntry(String sender, String message, Date date) {
        Check.objects(sender, message, date).notNull();
        this.sender = sender;
        this.message = message;
        this.date = date;
    }



    public DefaultOrionChatEntry(OrionChatEntry oce) {
        this(oce.getSender(), oce.getMessage(), oce.getDate());
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
}
