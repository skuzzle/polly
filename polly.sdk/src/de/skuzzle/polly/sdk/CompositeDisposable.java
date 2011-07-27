package de.skuzzle.polly.sdk;


import java.util.LinkedList;

import de.skuzzle.polly.sdk.exceptions.DisposingException;



/**
 * Class which provides disposing of a collection of other disposables.
 * 
 * @author Simon
 * @version 27.07.2011 5e9480b
 * @since Beta 0.5
 */
public class CompositeDisposable extends LinkedList<Disposable> implements Disposable {

    private static final long serialVersionUID = 1L;
    private boolean disposed;


    /**
     * Creates a new empty CompositeDisposable.
     */
    public CompositeDisposable() {}
    
    
    
    /**
     * Creates a new CompositeDisposable containing the given elements. The elements
     * will be disposed in the order they appear in this array.
     * @param disposables The disposable elements.
     */
    public CompositeDisposable(Disposable...disposables) {
        this(false, disposables);
    }
    
    
    
    /**
     * Creates a new CompositeDisposable containing the given elements. The elements
     * will be disposed in the order they appear in this array if {@code reverse} is
     * <code>true</code>. Otherwise, they will be disposed in reverse order.
     * @param disposables The disposable elements.
     */
    public CompositeDisposable(boolean reverse, Disposable...disposables) {
        for (Disposable disp : disposables) {
            if (reverse) {
                this.addFirst(disp);
            } else {
                this.addLast(disp);
            }
        }
    }
    
    

    /**
     * Disposes all elements in this collection without catching exceptions. If any
     * Exceptions occur while disposing an object, all further objects will not be
     * disposed. Also, this method does not regard the current disposed state as
     * returned by {@link #isDisposed()}.
     * @throws DisposingException If disposing af an element failed.
     */
    public synchronized void unsafeDispose() throws DisposingException {
        for (Disposable disp : this) {
            disp.dispose();
        }
    }

    
    
    /**
     * Disposes all elements in this collections. If an error occurs while disposing
     * an object, this method continues with disposing the next object. The last 
     * exception thrown while disposing will be delegated after all object have been
     * disposed.
     */
    @Override
    public synchronized void dispose() throws DisposingException {
        if (this.isDisposed()) {
            throw new IllegalStateException("already disposed.");
        }
        Exception error = null;
        for (Disposable disp : this) {
            if (disp.isDisposed()) { 
                continue; 
            }
            try {
                disp.dispose();
            } catch (Exception e) {
                error = e;
            }
        }
        if (error != null) {
            throw new DisposingException(error);
        }
    }



    @Override
    public synchronized boolean isDisposed() {
        return this.disposed;
    }
}
