package de.skuzzle.polly.sdk;

import java.util.List;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;

/**
 * <p>This is the easy to use polly persistence api.</p>
 * 
 * <p>Though its easy to use, there are a few important things to obey when working with
 * the <code>PPA</code>.</p>
 * <p>The most important issue is locking. The database is locked using thread-locking
 * methods. Whenever you read values from the database, you need to acquire a
 * readlock and release it when you are done. Same for writing to the database. You need
 * to take good care of releasing the locks especially in exceptional cases. Otherwise
 * you might run polly into a deadlock! See the examples below to learn how to 
 * prevent such cases.</p>
 * 
 * <p>The next thing to take care of is that you need to register all your entities to 
 * polly before you can use them. There is only one place during plugin initializing 
 * where this can happen, that is during executing the constructor 
 * {@link PollyPlugin#PollyPlugin(MyPolly)} using {@link #registerEntity(Class)}. All
 * later calls to this method will have no effect.</p>
 * 
 * <p>In order to use the persistence api, you need to include the javax.persistence 
 * package. Otherwise you wont be able do define your entities.</p>
 * 
 * 
 * This is how you use the PPA:
 * <pre>
 *     PersistenceManager persistence = myPolly.persistence();
 *     MyEntity e1 = new MyEntity();
 *     e1.setSomeValue(15);
 *     e1.setOtherValue("Peter");
 * 
 *     try {
 *         persistence.writeLock();
 *         persistence.startTransaction();
 *         persistence.persist(e1);     // no changes to the database 'til now...
 *         persistence.commitTransaction(); // now, the changes are taken to the db
 *     } catch (Exception e) {
 *         // react on exception. Your entity has not been persisted :(
 *     } finally {
 *         persistence.writeUnlock();
 *     }     
 * </pre>
 * Your entity is now managed in the persistence context. If you want to make updates
 * to that entity:
 * <pre>
 *     try {
 *         persistence.writeLock();
 *         persistence.startTransaction();
 *         e1.setSomeValue(17);
 *         e1.setOtherValue("Hans");
 *         persistence.commitTransaction();
 *     } catch (Exception e) {
 *         // error, no changes are persisted
 *     } finally {
 *         persistence.writeUnlock();
 *     }
 * </pre>
 * Or if you want to remove it from the database:
 * <pre>
 *     try {
 *         persistence.writeLock();
 *         persistence.startTransaction();
 *         persistence.remove(e1);
 *         persistence.commitTransaction();
 *     } catch (Exception e) {
 *         // error, nothing has been removed
 *     } finally {
 *         persistence.writeUnlock();
 *     }
 * </pre>
 * If you simply want to retrieve an entity using its primary key:
 * <pre>
 *     MyEntity e = null;
 *     try {
 *         persistence.readLock();
 *         e = persistence.find(MyEntity.class, 5); // find entity with id 5
 *     } finally {
 *         persistence.readUnlock();
 *     }
 *     if (e != null) {
 *         // entity has been found
 *     }
 * </pre>
 * 
 * Since SDK Version 0.6.4 the PersistenceManager supports atomic write operations
 * without external locking. That means you do not need to manually lock/unlock
 * the database. Its recommended that you update your implementations to use those new
 * atomic-Methods.
 * 
 * Deeper knowledge of the JPA is highly recommended for using the polly Persistence API.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface PersistenceManager {

    /**
     * <p>Registers an entity class to polly. This method must register all entities
     * you want to use during initiation of your plugin, that is during execution
     * of the PollyPlugin constructor.</p> 
     * 
     * <p>All later calls of this method will have no effect, resulting in an exception if 
     * you try to persist the entity.</p>
     * 
     * <p>All classes you register must have valid JPA annotations.</p>
     * 
     * @param clazz The class that should be registered to JPA as an entity.
     */
    public abstract void registerEntity(Class<?> clazz);
    
    
    
    /**
     * <p>Locks all other database operations for reading a value. This guarantees that
     * no values in the database change while the lock is active.</p>
     * 
     * <p>You must(!) release this lock after reading via {@link #readUnlock()}, otherwise
     * you will run whole polly into a deadlock.</p>
     */
    public abstract void readLock();
    
    
    
    /**
     * Releases a previously set readlock. This method may throw an exception if no
     * readlock is acquired and will most definitely throw an exception if the lock you
     * are trying to release is held by another thread.
     */
    public abstract void readUnlock();
    
    
    
    /**
     * <p>Locks all other database operations for writing a value. This guarantees that
     * no other operation currently reads the value you are trying to write.</p>
     * 
     * <p>You must(!) release this lock after writing you values via 
     * {@link #writeUnlock()}, otherwise you will run whole polly into a deadlock.</p>
     */
    public abstract void writeLock();
    
    
    
    /**
     * Releases a previously set writelock. This method may throw an exception if no
     * writelock is acquired and will most definitely throw an exception if the lock you
     * are trying to release is held by another thread.
     */
    public abstract void writeUnlock();
    
    
    
    /**
     * <p>Starts a new transaction. A transaction is an atomic database operation and is
     * required for adding and updating datasets. There may only be one active transaction
     * at once, therefore you need to lock the database access via the provided methods.
     * User {@link #writeLock()} to lock the database and {@link #writeUnlock()} for
     * unlocking.</p>
     * 
     * <p>After starting a transaction you can call {@link #persist(Object)} or
     * {@link #remove(Object)} as often as you want. All those changes will happen in
     * memory and are persisted in an atomic operation upon committing the transaction via 
     * {@link #commitTransaction()}.</p>
     * 
     * <p>If the commit fails, all changes are reverted - that means no actions are taken
     * to the actual database.</p>
     * 
     * @see #commitTransaction()
     */
    public abstract void startTransaction();
    
    
    
    /**
     * <p>Commits the active transaction and takes the changes to the database. This will
     * throw an error if no transaction is currently active. It assumes that the 
     * transaction is active.</p>
     * 
     * <p>If the commit fails the method tries to rollback the commit in order to revert
     * all changes and a DatabaseException is thrown.</p>
     * @throws DatabaseException If no transaction is currently active or if committing
     *      fails.
     */
    public abstract void commitTransaction() throws DatabaseException ;
    
    
    
    /**
     * <p>Persists the given object. If no transaction is currently active, calling this
     * method may at least result in doing nothing but may as well throw an exception. 
     * In other words: You have to take care of a transaction being active during 
     * calling <code>persist</code></p>
     * 
     * <p>You can only persist objects which you registered as an entity using 
     * {@link #registerEntity(Class)}.</p>
     * 
     * @param o The object to be persisted.
     */
    public abstract void persist(Object o);
    
    
    
    /**
     * Persists a whole list of entities.
     * 
     * @param <T> The entity type.
     * @param entities The entity list.
     * @see #persist(Object)
     */
    public abstract <T> void persistList(List<T> entities);
    
    
    
    /**
     * Finds an entity using its primary key.
     * 
     * Usage:
     * <pre>
     *  find(Employee.class, employeeId);
     * </pre>
     * @param type The entities type.
     * @param key The primary key of the entity to find.
     * @return The found entity or <code>null</code> of the primary key was not found.
     */
    public abstract <T> T find(Class<T> type, Object key);
    
    
    
    /**
     * Finds a single entity using a named query. This method may throw an 
     * Exception if the query returns more than one item.
     * 
     * @param type The entities type.
     * @param query The name of the named query. The query may only use numbered 
     *      parameters. 
     * @param params The parameter values for the query in order they appear in the
     *      query string.
     * @return The entity found or <code>null</code> if it was not found.
     */
    public abstract <T> T findSingle(Class<T> type, String query, Object...params);
    
    
    
    /**
     * Retrieves a whole list of entities from the database using a named query.
     * 
     * @param type The entities type.
     * @param query The name of the named query. The query may only use numbered 
     *      parameters. 
     * @param params The parameter values for the query in order they appear in the
     *      query string.
     * @return A list of entities matching the query. The list may be empty if no
     *      entity was found.
     */
    public abstract <T> List<T> findList(Class<T> type, String query, Object...params);
    
    
    
    /**
     * Retrieves a whole list of entities from the database using a named query.
     * 
     * @param type The entities type.
     * @param query The name of the named query. The query may only use numbered 
     *      parameters. 
     * @param params The parameter values for the query in order they appear in the
     *      query string.
     * @param limit The maximum amount of entities to retrieve.
     * @return A list of entities matching the query. The list may be empty if no
     *      entity was found.
     * @since 0.6.1
     */
    public abstract <T> List<T> findList(Class<T> type, String query, int limit, 
        Object...params);
    
    
    
    /**
     * Executes a native SQL-query.
     * 
     * @param query The query String in SQL syntax to execute.
     * @since 0.6.1
     */
    public abstract void executeNativeQuery(String query);
    
    
    
    /**
     * Removes an entity from the current persistence context.  If no transaction is 
     * currently active, calling this method may at least result in doing nothing but 
     * may as well throw an exception. In other words: You have to take care of a
     *  transaction being active during calling <code>remove</code>.
     *  
     * @param o The entity to remove.
     */
    public abstract void remove(Object o);
    
    
    /**
     * Removes a list of entities from the database using {@link #remove(Object)}
     * for every single entity from the list.
     * 
     * @param entities The entities to remove.
     * @since 0.6.4
     */
    public abstract <T> void removeList(List<T> entities);
    
    /**
     * Refreshes an object with data from the database. You may only call this method
     * for objects that are currently managed in the persistence context, otherwise
     * this method will throw an Exception.

     * @param o The object to refresh.
     */
    public abstract void refresh(Object o);
    
    
    
    /**
     * Drops the table for the specified entity. This method can only be called within
     * the {@link PollyPlugin#uninstall()} method. If called in another context, this
     * method will throw an exception.
     * 
     * @param tableName The name of the table to delete.
     * @throws DatabaseException If deleting the table fails or if you called this method
     *      from the wrong context.
     */
    public abstract void dropTable(String tableName) throws DatabaseException;
    
    
    
    /**
     * Performs an atomic write action. The database is locked for all read and 
     * write-accesses. Then the {@link WriteAction} is executed within a transaction.
     * Finally, the writelock is released. This is an example usage:
     * 
     * <pre>
     *     persistenceManager.atomicWrite(new WriteAction() {
     *         public void performUpdate(PersistenceManager persistence) {
     *             persistence.persist(myEntity);
     *             persistence.remove(otherEntity);
     *             // ... other write operations...
     *         }
     *     }
     * </pre>
     * 
     * @param persistence This {@link PersistenceManager} instance.
     * @throws DatabaseException If committing the transaction fails.
     * @since 0.6.4
     */
    public abstract void atomicWriteOperation(WriteAction action) 
            throws DatabaseException;
    
    
    
    /**
     * <p>Atomically persists the given entity using a transaction. Use this method if you
     * only need to persist a single entity. If you have to persist a list of entities
     * you should use {@link #atomicPersistList(List)} and if you need to perform further
     * write actions within a single transaction use 
     * {@link #atomicWriteOperation(WriteAction)} but do NOT use this method within a
     * WriteAction.</p>
     * 
     * There is no need for external synchronization using {@link #writeLock()} and
     * {@link #writeUnlock()}.
     * 
     * @param entity The entity to persist.
     * @throws DatabaseException If committing the transaction fails.
     * @since 0.6.4
     */
    public abstract void atomicPersist(final Object entity) 
            throws DatabaseException;
    
    
    
    /**
     * <p>Atomically persists the given entities using a single transaction. If you need 
     * to perform further write actions within a single transaction use 
     * {@link #atomicWriteOperation(WriteAction)} but do NOT use this method within
     * a write action</p>
     * 
     * <p>There is no need for external synchronization using {@link #writeLock()} and
     * {@link #writeUnlock()}.</p>
     * 
     * @param entity The entities to persist.
     * @throws DatabaseException If committing the transaction fails.
     * @since 0.6.4
     */
    public abstract <T> void atomicPersistList(List<T> entities) throws DatabaseException;
    
    
    
    /**
     * <p>Atomically removes the given entity using a single transaction. If you have
     * to perform further write actions within a single transaction you may use
     * {@link #atomicWriteOperation(WriteAction)} but do NOT use this method within
     * a {@link WriteAction}.
     * 
     * <p>There is no need for external synchronization using {@link #writeLock()} and
     * {@link #writeUnlock()}.</p>
     * 
     * @param entity The entity to remove.
     * @throws DatabaseException If committing the transaction fails.
     * @since 0.6.4
     */
    public abstract void atomicRemove(final Object entity) 
            throws DatabaseException;


    
    /**
     * <p>Atomically removes the given entities using a single transaction. If you need 
     * to perform further write actions within a single transaction use 
     * {@link #atomicWriteOperation(WriteAction)} but do NOT use this method within
     * a write action</p>
     * 
     * <p>There is no need for external synchronization using {@link #writeLock()} and
     * {@link #writeUnlock()}.</p>
     * 
     * @param entity The entities to remove.
     * @throws DatabaseException If committing the transaction fails.
     * @since 0.6.4
     */
    public void atomicRemove(List<Object> entities) throws DatabaseException;

    

    /**
     * Retrieves a single entity using its primary key. Its guaranteed that no database
     * changes happen while the entity is retrieved.
     * 
     * @param type Class-type of the entity to retrieve.
     * @param key Primary key of the entity to retrieve.
     * @return <code>null</code> if no entity with the given key exists. The retrieved
     *          entity otherwise.
     */
    public <T> T atomicRetrieveSingle(Class<T> type, Object key);
    
    
    
    /**
     * Retrieves a list of entities using a named query. Its guaranteed that no database 
     * changes happen while the entities are retrieved.
     * 
     * @param type Class-type of the entities to retrieve.
     * @param query The named query to select the entities.
     * @param params Parameters for that query.
     * @return A list of entities matching the query. The list may be empty if no
     *      entity was found.
     * @since 0.9.1
     */
    public <T> List<T> atomicRetrieveList(Class<T> type, String query, Object...params);
}
