package polly.util;

import java.util.Hashtable;
import java.util.List;
import javax.persistence.*;

import java.util.concurrent.locks.*;
import java.util.logging.Logger;



/**
 * A singleton wrapper for a JPA Entitymanager which connects to a given
 * persistence unit. It additionally provides methods for threadsafe working and easy
 * data retrieval from the database.
 * 
 * Use the method {@link #forUnit(String)} to create a new PersistenceProvider
 * instance. This will create und save an instance each time you call this method
 * with different persistence unit names. If you call it again with the same name it
 * will return the previously created instance. 
 * 
 * You will use the PersistenceProvider something like this:
 * <pre>
 *    PersistenceProvider persistence = PersistenceProvider.forUnit("yourUnit");
 *    
 *    //...
 *    
 *    EntityTransaction tx = null;
 *   
 *    try {
 *        // persistence.readLock();
 *        persistence.writeLock();
 *        tx = persistence.beginTransaction();
 *        
 *        // modifiy, delete or persist your entity here
 *        // ...
 *        
 *        tx.commit();           
 *    } catch (Exception e) {
 *        persistence.datebaseError(tx, e, "Persisting of %s failed: ...", 
 *                   x.toString(), logger);
 *    } finally {
 *        // persistence.readUnlock();
 *        persistence.wirteUnlock();
 *    }
 * </pre>
 * 
 * If you use several different persistence units within one project, you can acquire a
 * crosslock before any operations to synchronize operations between them.
 * 
 * @author Simon
 */
public class PersistenceProvider {
    
    
    public class DatabaseException extends RuntimeException {
        
        private static final long serialVersionUID = -2462774963671003135L;

        public DatabaseException(Exception cause) {
            super(cause);
        }
        
        
        
        public DatabaseException(String msg) {
            super(msg);
        }
    }
    
    
    private static final String LOG_COMMIT_FAILED = "Commit failed: %s";
    private static final String LOG_TRY_ROLLBACK = "Trying to rollback transaction.";
    private static final String LOG_ROLLBACK_FAILED = "Rollback failed: %s";
    private static final String LOG_ROLLBACK_SUCCESS = "Rollback successful.";
    
    
    private static Hashtable<String, PersistenceProvider> instances = 
            new Hashtable<String, PersistenceProvider>();
    
    private static ReentrantReadWriteLock crossLocker = new ReentrantReadWriteLock();
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private ReentrantReadWriteLock locker;
    private EntityTransaction activeTransaction;

    
    
    /**
     * Private constructor to create new PersistenceProvider.
     * 
     * @param persistenceUnit The name of the PersistenceUnit specified in 
     *      persistence.xml
     */
    private PersistenceProvider(String persistenceUnit) {
        this.emf = javax.persistence.Persistence.createEntityManagerFactory(
                persistenceUnit);
        this.em = this.emf.createEntityManager();
        this.locker = new ReentrantReadWriteLock();
    }
    
    
    
    /**
     * Acquires an reentrant lock for the current thread used for read operations.
     */
    public void readLock() {
        this.locker.readLock().lock();
    }
    
    
    
    /**
     * Releases the reentrant read lock for current thread.
     */
    public void readUnlock() {
        this.locker.readLock().unlock();
    }
    
    
    
    
    /**
     * Acquires an reentrant lock for the current thread used for write operations.
     */
    public void writeLock() {
        this.locker.writeLock().lock();
    }
    
    
    
    
    /**
     * Releases the reeantrant write lock for current thread.
     */
    public void writeUnlock() {
        this.locker.writeLock().unlock();
    }
    
    
    
    /**
     * Provides a static reentrant lock to synchronize read operations through two or more
     * PersistenceProvider instances.
     */
    public void crossReadLock() {
        PersistenceProvider.crossLocker.readLock().lock();
    }
    
    
    
    /**
     * Releases the cross read lock.
     * @see #crossLock()
     */
    public void crossReadUnlock() {
        PersistenceProvider.crossLocker.readLock().unlock();
    }
    
    
    
    /**
     * Provides a static reentrant lock to synchronize write operations through two or 
     * more PersistenceProvider instances.
     */
    public void crossWriteLock() {
        PersistenceProvider.crossLocker.writeLock().lock();
    }
    
    
    
    /**
     * Releases the cross write lock.
     * @see #crossLock()
     */
    public void crossWriteUnlock() {
        PersistenceProvider.crossLocker.writeLock().unlock();
    }
    
    
    
    /**
     * Initializes the instance with a given persistence unit.
     * 
     * @param persistenceUnit The persistence unit to be used by the new 
     *      PersistenceProvider instance.
     * @return The reference to a new or to an already existing PersistenceProvider
     *      instance. 
     */
    public static PersistenceProvider forUnit(String persistenceUnit) {
        PersistenceProvider instance = PersistenceProvider.instances.get(persistenceUnit);
        if (instance == null) {
            instance = new PersistenceProvider(persistenceUnit);
            PersistenceProvider.instances.put(persistenceUnit, instance);
        }
        return instance;
    }
    
    
    
    /**
     * Gets the underlying EntityManager instance. Its highly recommended to use this
     * rarely and carefully!
     * 
     * @return The EntityManager instance of the current PersistenceProvider.
     */
    public EntityManager getEntityManger() {
        return this.em;
    }
    
    
    
    /**
     * Wrapper for EntityManager.getTransaction.begin();
     * @return An EntityTransaction object.
     */
    public EntityTransaction beginTransaction() {
        this.activeTransaction = this.em.getTransaction();
        this.activeTransaction.begin();
        return this.activeTransaction;
    }
    
    
    
    /**
     * If a transaction is started via {@link #beginTransaction()}, this method returns
     * the current EntityTransaction. If no transaction is currently active, this
     * method returns null.
     * 
     * @return The currently active transaction or null if none is active.
     */
    public EntityTransaction getActiveTransaction() {
        return this.activeTransaction != null && this.activeTransaction.isActive() 
                ? this.activeTransaction
                : null;
    }
    
    
    
    /**
     * Wrapper for EntityManager.persist();
     * 
     * @param o Object to be persisted.
     */
    public void persist(Object o) {
        this.em.persist(o);
    }
    
    
    
    /**
     * Persists an entire list of entities.
     * 
     * @param entities The entities to persist.
     */
    public <T> void persistList(List<T> entities) {
        for (T entity : entities) {
            this.persist(entity);
        }
    }
    
    
    
    /**
     * Performs a simple insert of the object into the database. That is, locking the
     * database for write access, persisting the given object and then unlock the 
     * database. 
     * 
     * @param o The Object to persist.
     * @throws DatabaseException Thrown if persisting of the object fails for any reason.
     */
    public void simpleInsert(Object o) throws DatabaseException {
        EntityTransaction tx = null;
        try {
            this.writeLock();
            tx = this.beginTransaction();
            
            this.persist(o);
            tx.commit();
        } catch (Exception e) {
            this.databaseError(tx, e);
        } finally {
            this.writeUnlock();
        }
    }
    
    
    
    /**
     * Performs a simple deletion of the given object from the database. that is, locking
     * the database for write access, removing the object and then unlock the database.
     * 
     * @param o The object to delete from the database.
     * @throws DatabaseException Thrown if deleting fails for any reason.
     */
    public void simpleDelete(Object o) throws DatabaseException {
        EntityTransaction tx = null;
        try {
            this.writeLock();
            tx = this.beginTransaction();
            
            this.remove(o);
            
            tx.commit();
        } catch (Exception e) {
            this.databaseError(tx, e);
        } finally {
            this.writeUnlock();
        }
    }
    
    
    
    /**
     * Wrapper for EntityManager.find();
     * 
     * @param <T> The generic returntype determined by parameter type.
     * @param type The Entities type.
     * @param key The primary key of the entitie to retrieve.
     * @return The found entity if any or null if none was found.
     */
    public <T> T find(Class<T> type, Object key) {
        return this.em.find(type, key);
    }
    
    
    
    /**
     * Wrapper for retrieving an entire list from the Database.
     * 
     * @param <T> The generic return type determined by parameter type.
     * @param type The type of the list that shall be retrieved.
     * @param queryName The String-Name of the NamedQuery that shall be executed to get 
     *      the list. The query-string requires to have numbered arguments only.
     * @param maxResults Maximum of results to return
     * @param params The parameters for the given NamedQuery in order of their numbers.
     * @return A list of entities of given type found by given query.   
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findList(Class<T> type, String queryName, 
                int maxResults, Object...params) {
        
        Query q = this.em.createNamedQuery(queryName);
        int i = 1;
        for (Object param : params) {
            q.setParameter(i++, param);
        }
        
        q.setMaxResults(maxResults);

        return q.getResultList();
    }
    
    
    
    /**
     * Wrapper for retrieving an entire list from the Database.
     * 
     * @param <T> The generic return type determined by parameter type.
     * @param type The type of the list that shall be retrieved.
     * @param queryName The String-Name of the NamedQuery that shall be executed to get 
     *      the list. The query-string requires to have numbered agruments only.
     * @param params The parameters for the given NamedQuery in order of their numbers.
     * @return A list of entities of given type found by given query.   
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findList(Class<T> type, String queryName, Object...params) {
        Query q = this.em.createNamedQuery(queryName);
        int i = 1;
        for (Object param : params) {
            q.setParameter(i++, param);
        }
        
        return q.getResultList();
    }
    
    
    
    /**
     * Wrapper for finding single entities via named queries.
     * 
     * @param <T> The generic return type determined by parameter type.
     * @param type The Entities type.
     * @param queryName The string-name of the NamedQuery that shall be executed to get 
     *      the entity. The query-string requires to have numbered agruments only.
     * @param params The parameters for the given NamedQuery in order of their numbers.
     * @return The retrieved entity if any, otherwise null.
     * @throws NonUniqueResultException if more than one result.
     * @throws IllegalStateException if called for a Java Persistence query language 
     *      UPDATE or DELETE statement.
     */
    @SuppressWarnings("unchecked")
    public <T> T findByQuery(Class<T> type, String queryName, Object... params) 
                throws NonUniqueResultException, IllegalStateException {
        
        Query q = this.em.createNamedQuery(queryName);
        int i = 1;
        for (Object param : params) {
            q.setParameter(i++, param);
        }
        
        T result;
        try {
            result = (T) q.getSingleResult();
        } catch (NoResultException e) {
            result = null;
        }
        
        return result;
    }
    
    
    
    /**
     * Wrapper for finding single entities via named queries. This does the same as
     * the {@link #findByQuery(Class, String, Object...)} method but does not throw
     * any exceptions. Instead it will return null on any exception.
     * 
     * @param <T> The generic return type determined by parameter type.
     * @param type The Entities type.
     * @param queryName The string-name of the NamedQuery that shall be executed to get 
     *      the entity. The query-string requires to have numbered agruments only.
     * @param params The parameters for the given NamedQuery in order of their numbers.
     * @return The retrieved entity if any, otherwise null.
     */
    public <T> T tryfindByQuery(Class<T> type, String queryName, Object... params) {
        T result = null;
        
        try {
            result = this.findByQuery(type, queryName, params);
        } catch (Exception e) {
            result = null;
        }
        
        return result;
    }
    
    
    
    /**
     * Executes a given native query. 
     * @param query The query to execute (only DELETE and UPDATE)
     */
    public void executeNativeQuery(String query) {
        Query q = this.em.createNativeQuery(query);
        
        q.executeUpdate();
    }
    
    
    
    /**
     * Executes an update or delete query.
     * 
     * @param queryName The string-name of the NamedQuery that shall be executed.
     * @param params The parameters for the given NamedQuery in order of their numbers.
     */
    public void executeQuery(String queryName, Object... params) {
        Query q = this.em.createNamedQuery(queryName);
        int i = 1;
        for (Object param : params) {
            q.setParameter(i++, param);
        }
        
        q.executeUpdate();
    }
    
    
    
    /**
     * Wrapper for EntityManager.remove()
     * 
     * @param o Object to be removed from the database.
     */
    public void remove(Object o) {
        this.em.remove(o);
    }
    
    
    
    /**
     * Removes a list of objects from the database.
     * @param objects The Objects to remove
     */
    public <T> void removeAll(List<?> objects) {
        for (Object o : objects) {
            this.remove(o);
        }
    }
    
    
    
    /**
     * Failsafe wrapper for EntityManager.remove(). This means, if called for a non
     * managed object, nothing happens.
     * 
     * @param o The object to be refreshed.
     */
    public void refresh(Object o) {
        if (this.contains(o)) {
            this.em.refresh(o);
        }
    }
    
    
    
    /**
     * Refreshes a whole list of objects. 
     * 
     * @param os The objects to refresh.
     */
    public <T> void refreshAll(List<?> objects) {
        for (Object o : objects) {
            this.refresh(o);
        }
    }
    
    
    
    /**
     * Wrapper for EntityManager.contains()
     * Checks whether given object is attached to current persistence context.
     * 
     * @param o Object to check.
     * @return True if given objects state is 'attached', false otherwise.
     */
    public boolean contains(Object o) {
        return this.em.contains(o);
    }
    

    
    /**
     * Handles an error which may occur during committing of an transaction and tries
     * to rollback the transaction. This method will always throw an DatabaseException.
     * 
     * @param tx The transaction which failed.
     * @param e The occurred exception which caused the transaction to fail.
     * @param errorMessage The string to be returned within DatabaseException.
     * @param param A parameter for the errorstring.
     * @param logger A logger instance to ouput error log messages.
     * @throws DatabaseException Always thrown by this method.
     */
    public void databaseError(EntityTransaction tx, Exception e, 
            String errorMessage, Object param, Logger logger) throws DatabaseException {
        
        logger.severe(String.format(LOG_COMMIT_FAILED, e.getMessage()));
        if (tx != null && tx.isActive()) {
            try {
                logger.info(LOG_TRY_ROLLBACK);
                tx.rollback();
                logger.info(LOG_ROLLBACK_SUCCESS);
            } catch (Exception e1) {
                logger.severe(
                        String.format(LOG_ROLLBACK_FAILED, e1.getMessage()));
            }
        }
        throw new DatabaseException(String.format(errorMessage, param));
    }
    
    
    
    /**
     * Handles an error which may occur during committing of an transaction and tries
     * to rollback the transaction. This method will always throw an DatabaseException.
     * 
     * @param tx The transaction which failed.
     * @param e The occurred exception which caused the transaction to fail.
     * @throws DatabaseException Always thrown by this method.
     */
    public void databaseError(EntityTransaction tx, Exception e) 
            throws DatabaseException {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (Exception e1) {

            }
        }
        throw new DatabaseException(e);
    }
}