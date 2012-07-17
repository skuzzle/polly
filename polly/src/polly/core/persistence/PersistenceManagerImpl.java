package polly.core.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;

/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class PersistenceManagerImpl extends AbstractDisposable implements
    PersistenceManager {

    private static Logger logger = Logger
        .getLogger(PersistenceManagerImpl.class.getName());
    private static ReentrantReadWriteLock crossLocker = new ReentrantReadWriteLock();

    private EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction activeTransaction;
    private EntityList entities;



    public PersistenceManagerImpl() {
        this.entities = new EntityList();
    }



    public void connect(String persistenceUnit) {
        logger.info("Connecting to persistence unit '" + persistenceUnit + "'...");

        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
        this.em = this.emf.createEntityManager();

        logger.info("Database connection established.");
    }



    @Override
    protected void actualDispose() throws DisposingException {
        logger.debug("Shutting down database...");
        logger.trace("Shutting down entity manager...");
        try {
            logger.trace("Waiting for all operations to end...");
            this.writeLock();
            if (this.em.isOpen()) {
                try {
                    logger.trace("Sending SHUTDOWN command.");
                    this.startTransaction();
                    Query q = this.em.createNativeQuery("SHUTDOWN");
                    q.executeUpdate();
                    this.commitTransaction();
                } catch (Exception e) {
                    logger.error("SHUTDOWN command failed.");
                }
                this.em.close();
                this.em = null;
            }

            logger.trace("Shutting down entity manager factory...");
            if (this.emf.isOpen()) {
                this.emf.close();
                this.emf = null;
            }

            logger.debug("Database connection closed.");
        } catch (Exception e) {
            logger.fatal("Error while shutting down database.");
        } finally {
            this.writeUnlock();
        }
    }



    public EntityList getEntities() {
        return this.entities;
    }



    @Override
    public void registerEntity(Class<?> clazz) {
        logger.debug("Registering new entity: " + clazz.getName());
        this.entities.add(clazz);
    }



    @Override
    public void readLock() {
        logger.trace("Acquiring read lock...");
        crossLocker.readLock().lock();
        logger.trace("Got readlock.");
    }



    @Override
    public void readUnlock() {
        crossLocker.readLock().unlock();
        logger.trace("Readlock released.");
    }



    @Override
    public void writeLock() {
        logger.trace("Acquiring write lock...");
        crossLocker.writeLock().lock();
        logger.trace("got write lock.");
    }



    @Override
    public void writeUnlock() {
        crossLocker.writeLock().unlock();
        logger.trace("Writelock released.");
    }



    @Override
    public void startTransaction() {
        logger.debug("Starting transaction...");
        this.activeTransaction = this.em.getTransaction();
        this.activeTransaction.begin();
        logger.debug("Transaction started.");
    }



    @Override
    public void commitTransaction() throws DatabaseException {
        logger.debug("Committing transaction...");
        EntityTransaction tx = this.activeTransaction;
        if (tx == null) {
            throw new DatabaseException("No transaction active.");
        }

        try {
            tx.commit();
        } catch (Exception e) {
            logger.error("Committing transaction failed.", e);
            if (tx != null && tx.isActive()) {
                try {
                    logger.debug("Trying to rollback transaction.");
                    tx.rollback();
                    logger.debug("Rollback successful.");
                } catch (Exception e1) {
                    logger.fatal("Rollback failed!", e1);
                }
            }
            throw new DatabaseException("Transaction failed", e);
        }
        logger.debug("Transaction finished successful");
    }



    @Override
    public void persist(Object o) {
        logger.trace("Persisting " + o.getClass().getName() + "("
            + o.toString() + ")");
        this.em.persist(o);
    }



    @Override
    public <T> void persistList(List<T> entities) {
        for (T entity : entities) {
            this.persist(entity);
        }
    }



    @Override
    public <T> T find(Class<T> type, Object key) {
        logger.trace("Looking up primary key " + key + " in " + type.getName());
        return this.em.find(type, key);
    }



    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findList(Class<T> type, String query, Object... params) {
        logger.trace("Executing named query '" + query + "'. Parameters: "
            + Arrays.toString(params));

        Query q = this.em.createNamedQuery(query);
        int i = 1;
        for (Object param : params) {
            q.setParameter(i++, param);
        }

        return q.getResultList();
    }



    @Override
    public void executeNativeQuery(String query) {
        Query q = this.em.createNativeQuery(query);
        q.executeUpdate();
    }



    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findList(Class<T> type, String query, int limit,
        Object... params) {
        logger.trace("Executing named query '" + query + "'. Parameters: "
            + Arrays.toString(params) + ", limit: " + limit);

        Query q = this.em.createNamedQuery(query);
        q.setMaxResults(limit);
        int i = 1;
        for (Object param : params) {
            q.setParameter(i++, param);
        }

        return q.getResultList();
    }



    @Override
    public void remove(Object o) {
        logger.trace("Removing " + o.getClass().getName() + "(" + o.toString()
            + ")");
        this.em.remove(o);
    }



    @Override
    public void removeList(List<Object> entities) {
        for (Object o : entities) {
            this.remove(o);
        }
    }



    @SuppressWarnings("unchecked")
    @Override
    public <T> T findSingle(Class<T> type, String query, Object... params) {
        Query q = this.em.createNamedQuery(query);

        int i = 1;
        for (Object param : params) {
            q.setParameter(i++, param);
        }

        try {
            return (T) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }



    @Override
    public void dropTable(String tableName) throws DatabaseException {
        this.checkStack(PollyPlugin.class, "uninstall");
        String query = "TRUNCATE TABLE " + tableName;
        Query dropQuery = this.em.createNativeQuery(query);
        try {
            this.writeLock();
            dropQuery.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseException("Droping failed", e);
        } finally {
            this.writeUnlock();
        }
    }



    private void checkStack(Class<?> cls, String methodName)
        throws DatabaseException {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        if (stes.length == 0) {
            throw new DatabaseException(
                "This method cannot be called in this context.");
        }

        StackTraceElement ste = stes[0];
        try {
            Class<?> clazz = Class.forName(ste.getClassName());
            if (!ste.getMethodName().equals(methodName)
                || !cls.isAssignableFrom(clazz)) {
                throw new DatabaseException(
                    "This method cannot be called in this context.");
            }
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(
                "This method cannot be called in this context.");
        }
    }



    @Override
    public void atomicWriteOperation(WriteAction action)
            throws DatabaseException {
        try {
            this.writeLock();
            this.startTransaction();
            action.performUpdate(this);
            
        } finally {
            try {
                this.commitTransaction();
            } finally {
                this.writeUnlock();
            }
        }
    }



    @Override
    public void atomicPersist(final Object entity) throws DatabaseException {
        this.atomicWriteOperation(new WriteAction() {

            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.persist(entity);
            }
        });
    }



    @Override
    public <T> void atomicPersistList(final List<T> entities)
        throws DatabaseException {
        this.atomicWriteOperation(new WriteAction() {

            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.persistList(entities);
            }
        });
    }



    @Override
    public void atomicRemove(final Object entity) throws DatabaseException {
        this.atomicWriteOperation(new WriteAction() {

            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.remove(entity);
            }
        });
    }



    @Override
    public void atomicRemove(final List<Object> entities)
        throws DatabaseException {
        this.atomicWriteOperation(new WriteAction() {

            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.removeList(entities);
            }
        });
    }



    @Override
    public <T> T atomicRetrieveSingle(Class<T> type, Object key) {
        try {
            this.readLock();
            return this.find(type, key);
        } finally {
            this.readUnlock();
        }
    }
    
    
    
    @Override
    public <T> List<T> atomicRetrieveList(Class<T> type, String query, Object...params) {
        try {
            this.readLock();
            return this.findList(type, query, params);
        } finally {
            this.readUnlock();
        }
    }



    @Override
    public void refresh(Object o) {
        this.em.refresh(o);
    }
}
