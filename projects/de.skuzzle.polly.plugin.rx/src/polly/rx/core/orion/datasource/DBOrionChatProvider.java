package polly.rx.core.orion.datasource;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import polly.rx.core.orion.OrionChatProvider;
import polly.rx.core.orion.model.DefaultOrionChatEntry;
import polly.rx.core.orion.model.OrionChatEntry;
import polly.rx.entities.DBOrionChatEntry;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.collections.TemporaryValueMap;

public class DBOrionChatProvider implements OrionChatProvider {

    
    private final static long ACTIVITY_TIMEOUT = Milliseconds.fromMinutes(2);
    
    private final Map<String, Date> activityMap;
    private final PersistenceManagerV2 persistence;



    public DBOrionChatProvider(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
        this.activityMap = new TemporaryValueMap<>(ACTIVITY_TIMEOUT);
    }



    @Override
    public void addChatEntry(OrionChatEntry oce, boolean noteActivity) 
            throws DatabaseException {
        if (noteActivity) {
            this.noteActivity(oce.getSender());
        }
        final DBOrionChatEntry dboce = new DBOrionChatEntry(oce);
        try (final Write w = this.persistence.write()) {
            w.single(dboce);
        }
    }
    
    
    
    private Date noteActivity(String sender) {
        return this.activityMap.put(sender, Time.currentTime());
    }

    
    
    @Override
    public Set<String> getActiveNicknames() {
        return this.activityMap.keySet();
    }
    


    @Override
    public List<DefaultOrionChatEntry> getYoungestEntries(String receiver, 
            boolean isPoll, int max) {
        final List<DBOrionChatEntry> entries = this.persistence.atomic().findList(
                DBOrionChatEntry.class, DBOrionChatEntry.YOUNGEST_ENTRIES, max);
        final Date prevActivity = this.noteActivity(receiver);
        
        Stream<DefaultOrionChatEntry> s = entries.stream()
                .sorted(Comparator.comparing(DBOrionChatEntry::getId))
                .map(DefaultOrionChatEntry::new);
        if (prevActivity != null && isPoll) {
            s = s.filter(e -> e.getDate().compareTo(prevActivity) >=0);
        }
        return s.collect(Collectors.toList());
    }
}