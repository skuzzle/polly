package de.skuzzle.polly.tools.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Ringbuffer implementation which is backed by a {@link LinkedList}. The buffer has a
 * fix capacity. When the capacity is reached, for each new element that is being added,
 * the oldest existing element will be removed from the buffer.
 * 
 * @author Simon Taddiken
 * @param <T> Types of elements in this buffer.
 */
public class LinkedRingBuffer<T> implements Queue<T> {

    private LinkedList<T> q;
    private int capacity;

    /**
     * Creates a new LinkedRingBuffer with the given capacity.
     * 
     * @param capacity Capacity of this buffer.
     */
    public LinkedRingBuffer(int capacity) {
        super();
        this.capacity = capacity;
        this.q = new LinkedList<T>();
    }
    
    

    /**
     * Creates a new LinkedRingBuffer with given capacity. The elements of the given
     * collection are initially added to the new buffer. If the size of the given 
     * collection is greater than the buffer's capacity, only the last 
     * <code>capacity</code> elements will be added to the buffer.
     * 
     * @param c Collection to initially add to the buffer.
     * @param capacity Capacity of this buffer.
     */
    public LinkedRingBuffer(Collection<? extends T> c, int capacity) {
        this(capacity);
        this.addAll(c);
    }
    
    
    
    /**
     * Gets the capacity of this buffer. This is the maximum number of elements this 
     * buffer can hold.
     * 
     * @return The capacity.
     */
    public int getCapacity() {
        return this.capacity;
    }



    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (final T t : c) {
            modified |= this.add(t);
        }
        return modified;
    }



    @Override
    public void clear() {
        this.q.clear();
    }



    @Override
    public boolean contains(Object o) {
        return this.q.contains(o);
    }



    @Override
    public boolean containsAll(Collection<?> c) {
        return this.q.containsAll(c);
    }



    @Override
    public boolean isEmpty() {
        return this.q.isEmpty();
    }



    @Override
    public Iterator<T> iterator() {
        return this.q.descendingIterator();
    }



    @Override
    public boolean remove(Object o) {
        return this.q.remove(o);
    }



    @Override
    public boolean removeAll(Collection<?> c) {
        return this.q.removeAll(c);
    }



    @Override
    public boolean retainAll(Collection<?> c) {
        return this.q.retainAll(c);
    }



    @Override
    public int size() {
        return this.q.size();
    }



    @Override
    public Object[] toArray() {
        return this.q.toArray();
    }



    @Override
    public <E> E[] toArray(E[] a) {
        return this.q.toArray(a);
    }



    @Override
    public boolean add(T e) {
        if (this.q.size() == this.capacity) {
            this.q.poll();
        }
        return this.q.add(e);
    }



    @Override
    public T element() {
        return this.q.element();
    }



    @Override
    public boolean offer(T e) {
        return this.add(e);
    }



    @Override
    public T peek() {
        return this.q.peek();
    }



    @Override
    public T poll() {
        return this.q.poll();
    }



    @Override
    public T remove() {
        return this.q.remove();
    }
}