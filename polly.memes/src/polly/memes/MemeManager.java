package polly.memes;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;


public class MemeManager {

    private PersistenceManager persistence;
    
    public MemeManager(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    
    
    public void addMeme(MemeEntity meme) throws DatabaseException {
        this.persistence.atomicPersist(meme);
    }
    
    
    
    public void deleteMeme(String name) throws CommandException, DatabaseException {
        MemeEntity meme = this.getMeme(name);
        
        this.persistence.atomicRemove(meme);
    }
    
    
    
    public boolean checkMemeExists(String name) {
        return this.persistence.findSingle(MemeEntity.class, 
            MemeEntity.MEME_BY_NAME, name) != null;
    }
    
    
    public MemeEntity getMeme(String name) throws CommandException {
        MemeEntity meme = this.persistence.findSingle(MemeEntity.class, 
                MemeEntity.MEME_BY_NAME, name);
        if (meme == null) {
            throw new CommandException("Unbekanntes Meme: " + name);
        }
        return meme;
    }
}