package polly.rx.core.orion.datasource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import polly.rx.core.orion.OrionChatProvider;
import polly.rx.core.orion.model.DefaultOrionChatEntry;
import polly.rx.core.orion.model.OrionChatEntry;
import polly.rx.entities.DBOrionChatEntry;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;

public class DBOrionChatProvider implements OrionChatProvider {

    private final PersistenceManagerV2 persistence;



    public DBOrionChatProvider(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }



    @Override
    public void addChatEntry(OrionChatEntry oce) throws DatabaseException {
        final DBOrionChatEntry dboce = new DBOrionChatEntry(oce);
        try (final Write w = this.persistence.write()) {
            w.single(dboce);
        }
    }



    @Override
    public List<? extends OrionChatEntry> getYoungestEntries(int max) {
        final List<DBOrionChatEntry> entries = this.persistence.atomic().findList(
                DBOrionChatEntry.class, DBOrionChatEntry.YOUNGEST_ENTRIES, max);
        return entries.stream()
                .sorted(Comparator.comparing(DBOrionChatEntry::getId))
                .map(DefaultOrionChatEntry::new)
                .collect(Collectors.toList());
    }
}