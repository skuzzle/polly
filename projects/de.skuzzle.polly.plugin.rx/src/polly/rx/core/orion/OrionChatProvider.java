package polly.rx.core.orion;

import java.util.List;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import polly.rx.core.orion.model.OrionChatEntry;


public interface OrionChatProvider {

    public void addChatEntry(OrionChatEntry oce) throws DatabaseException;
    
    public List<? extends OrionChatEntry> getYoungestEntries(int max);
}