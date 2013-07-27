package de.skuzzle.polly.sdk;

import java.util.List;


/**
 * <p>Use this class in conjunction with 
 * {@link PersistenceManager#registerEntityConverter(EntityConverter)}</p>
 * 
 * <p>With an EntityConverter you are able to update outdated entities your plugin used
 * in a previous version to a new version. Optionally you may specify that all the old 
 * entities shall be removed from the database.</p>
 * 
 * @author Simon
 * @since 0.9.1
 */
public interface EntityConverter {

    /**
     * <p>This method must return a list of all old entities that should be converted to 
     * new ones.</p>
     * 
     * <p>Please note that you must not use the atomic methods of the passed persistence
     * manager, as synchronization is done automatically when polly executes this 
     * entity converter.</p>
     * 
     * @param persistence A PersistenceManager instance to retrieve the old entities with.
     * @return A list of old entities.
     */
    public abstract List<Object> getOldEntities(PersistenceManager persistence);
    
    
    
    /**
     * This method gets called for each entity that was retrieved with 
     * {@link #getOldEntities(PersistenceManager)} and should convert the passed object
     * into a matching instance of the new entity type.
     * 
     * @param old The old entity.
     * @return A new, converted entity.
     */
    public abstract Object convertEntity(Object old);
    
    
    
    /**
     * <p>After converting all your entities, polly calls this method to remove the old 
     * entities, but you may also choose to leave your implementation of this method
     * empty so nothing happens to the old entities.</p>
     * 
     * <p>Please note that you must not use the atomic methods of the passed persistence
     * manager, as synchronization is done automatically when polly executes this 
     * entity converter.</p>
     * 
     * @param olds Collection of the old entities that exists in the database.
     * @param persistence A PersistenceManager instance to remove the old entities with.
     */
    public abstract void deleteOldEntities(List<Object> olds, 
        PersistenceManager persistence);
}