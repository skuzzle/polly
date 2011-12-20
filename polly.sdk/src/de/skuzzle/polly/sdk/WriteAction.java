package de.skuzzle.polly.sdk;

/**
 * <p>This class represents a simple action that can be atomically executed by the
 * {@link PersistenceManager}. Its action will ONLY be executed atomically when you
 * use the {@link PersistenceManager#atomicWriteOperation(PersistenceManager)}
 * method.</p>
 * 
 *  Sample usage:
<pre>
    // ...
    PersistenceManager persistence = this.getMyPolly().persistence();
    
    persistence.atomicWriteOperation(new WriteAction() {
    
        public void performWrite(PersistenceManager persistence) {
            persistence.persist(myEntity);
        }
    };
    // ...
</pre>

 * @author Simon
 * @since 0.6.4
 */
public interface WriteAction {

    /**
     * Performs an atomic write on the database if used with 
     * {@link PersistenceManager#atomicWriteOperation(PersistenceManager)}.
     * 
     * @param persistence The {@link PersistenceManager} instance on which this
     *          WriteAction is used.
     */
    public abstract void performUpdate(PersistenceManager persistence);
}