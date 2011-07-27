package de.skuzzle.polly.sdk;

import de.skuzzle.polly.sdk.exceptions.DisposingException;


/**
 * Abstract class for thread-safe implementation of the {@link Disposable} interface. Once
 * disposed, it cannot be disposed again. This would result in an 
 * {@link IllegalStateException} being thrown as by the contract of the Disposable 
 * interface.
 * 
 * @author Simon
 * @version 27.07.2011 5e9480b
 * @since Beta 0.5
 */
public abstract class AbstractDisposable implements Disposable {

    private boolean disposed;
    
    
    
    /**
     * Determines whether this object already has been disposed.
     * @return If this object is disposed.
     */
    public final synchronized boolean isDisposed() {
        return this.disposed;
    }
    
    
    
    /**
     * Method to dispose your object. You See {@link Disposable#dispose()}. You are
     * not required to take care of exceptions being thrown by this method. This is
     * done by the final implementation of {@link #dispose()}. Anyway, you can throw
     * an exception, which then will be delegated by <code>dispose()</code>.
     */
    protected abstract void actualDispose() throws DisposingException;
    
    
    
    /**
     * <p>This method does the actual disposing of your object, disregarding any 
     * exceptions thrown by {@link #actualDispose()}. That means that thrown exceptions 
     * are caught and delegated as a {@link DisposingException} to the calling method.</p>
     * 
     * Note that disposed state for this object is set even if an exception is thrown
     * during disposing.
     * @throws DisposingException If disposing fails.
     * @throws IllegalStateException If this object already is disposed.
     */
    @Override
    public final synchronized void dispose() throws DisposingException {
        if (this.isDisposed()) {
            throw new IllegalStateException("already disposed.");
        }
        this.disposed = true;
        try {
            this.actualDispose();
        } catch (DisposingException e) {
            throw e;
        } catch (Exception e) {
            throw new DisposingException(e);
        }
    }

}
