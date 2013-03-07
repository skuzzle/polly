package de.skuzzle.polly.tools.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class LinkedRingBuffer<T> implements Queue<T> {

    private LinkedList<T> q;
    private int capacity;

    
    public LinkedRingBuffer(int capacity) {
        super();
        this.capacity = capacity;
        this.q = new LinkedList<T>();
    }
    
    

    public LinkedRingBuffer(Collection<? extends T> c, int capacity) {
        this(capacity);
        this.addAll(c);
    }
    
    
    
    public int getCapacity() {
        return this.capacity;
    }



    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.q.addAll(c);
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