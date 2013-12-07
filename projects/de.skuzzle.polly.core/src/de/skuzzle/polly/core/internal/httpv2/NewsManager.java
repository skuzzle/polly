package de.skuzzle.polly.core.internal.httpv2;

import java.util.List;

import de.skuzzle.polly.core.internal.users.UserImpl;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.time.Time;


public class NewsManager {
    
    public final static String ADD_NEWS_PERMISSION = "polly.permission.ADD_NEWS"; //$NON-NLS-1$
    public final static String EDIT_NEWS_PERMISSION = "polly.permission.EDIT_NEWS"; //$NON-NLS-1$
    public final static String DELETE_NEWS_PERMISSION = "polly.permission.DELETE_NEWS"; //$NON-NLS-1$

    
    private final PersistenceManagerV2 persistence;
    
    
    public NewsManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }
    
    
    
    public void addNewsEntry(User executor, String caption, String body) 
            throws DatabaseException {
        try (final Write write = this.persistence.write()) {
            final NewsEntry ne = new NewsEntry((UserImpl) executor, caption, body, 
                    Time.currentTime());
            write.single(ne);
        }
    }
    
    
    
    public void deleteNewsEntry(int newsId) throws DatabaseException {
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();
            
            final NewsEntry ne = read.find(NewsEntry.class, newsId);
            
            if (ne == null) {
                throw new DatabaseException(MSG.newsUnknownId);
            }
            
            write.remove(ne);
        }
    }
    
    
    
    public List<NewsEntry> getAllNews() {
        return this.persistence.atomic().findList(NewsEntry.class, 
                NewsEntry.QUERY_GET_ALL_NEWS);
    }
}
