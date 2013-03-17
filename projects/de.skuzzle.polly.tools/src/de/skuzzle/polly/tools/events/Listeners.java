package de.skuzzle.polly.tools.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

/**
 * Collection class for {@link EventListener}s. Despite being a normal collection, this 
 * class knows what kind of listeners it contains and is thus able to remove a certain
 * listener from its parent {@link EventProvider}.
 * 
 * @author Simon
 * @param <T> Type of listeners that are contained in this collection.
 */
public class Listeners<T extends EventListener> implements Collection<T> {

    private final List<T> backend;
    private final Class<T> eventClass;
    private final EventProvider parent;
    
    
    
    Listeners(Collection<T> c, Class<T> eventClass, EventProvider parent) {
        this.backend = new ArrayList<T>(c);
        this.eventClass = eventClass;
        this.parent = parent;
    }
    
    
    
    public void removeFromParent(T listener) {
        this.parent.removeListener(this.eventClass, listener);
    }
    
    
    
    @Override
    public boolean add(T e) {
        return this.backend.add(e);
    }
    
    

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.backend.addAll(c);
    }
    
    

    @Override
    public void clear() {
        this.backend.clear();
    }

    
    
    @Override
    public boolean contains(Object o) {
        return this.backend.contains(o);
    }
    
    

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.backend.containsAll(c);
    }

    
    
    @Override
    public boolean isEmpty() {
        return this.backend.isEmpty();
    }
    
    

    @Override
    public Iterator<T> iterator() {
        return this.backend.iterator();
    }
    
    

    @Override
    public boolean remove(Object o) {
        return this.backend.remove(o);
    }

    
    
    @Override
    public boolean removeAll(Collection<?> c) {
        return this.backend.removeAll(c);
    }

    
    
    @Override
    public boolean retainAll(Collection<?> c) {
        return this.backend.retainAll(c);
    }

    
    
    @Override
    public int size() {
        return this.backend.size();
    }

    
    
    @Override
    public Object[] toArray() {
        return this.backend.toArray();
    }

    
    
    @Override
    public <E> E[] toArray(E[] a) {
        return this.backend.toArray(a);
    }
}
