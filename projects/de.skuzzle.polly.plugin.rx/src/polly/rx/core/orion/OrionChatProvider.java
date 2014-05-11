package polly.rx.core.orion;

import java.util.List;
import java.util.Set;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import polly.rx.core.orion.model.DefaultOrionChatEntry;
import polly.rx.core.orion.model.OrionChatEntry;


public interface OrionChatProvider {

    public void addChatEntry(OrionChatEntry oce, boolean noteActivity) 
            throws DatabaseException;
    
    public Set<String> getActiveNicknames();
    
    public List<DefaultOrionChatEntry> getYoungestEntries(String receiver, 
            boolean isPoll, int max);
}