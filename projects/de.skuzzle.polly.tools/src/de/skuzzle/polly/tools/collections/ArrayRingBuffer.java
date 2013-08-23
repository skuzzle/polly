package de.skuzzle.polly.tools.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;


public class ArrayRingBuffer<T> implements Queue<T> {
    
    private final T[] buffer;
    private final int cap;
    private int size;
    private int start;
    private int modCount;
    
    
    
    @SuppressWarnings("unchecked")
    public ArrayRingBuffer(int capacity) {
        this.buffer = (T[]) new Object[capacity];
        this.cap = capacity;
        this.start = 0;
        this.size= 0;
    }
    
    
    
    public boolean isFilled() {
        return this.size == this.cap - 1;
    }
    
    
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.buffer) + 31 * this.size + 37 * this.start;
    }
    
    
    
    @Override
    public boolean equals(Object o) {
        ArrayRingBuffer<?> other = null;
        return o == this || o != null &&
            o instanceof ArrayRingBuffer &&
            Arrays.equals((other = (ArrayRingBuffer<?>) o).buffer, this.buffer) &&
            other.size == this.size &&
            other.start == this.start;
    }
    
    
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("[");
        final Iterator<T> it = this.iterator();
        while (it.hasNext()) {
            final T next = it.next();
            if (next != this) {
                b.append(next.toString());
            } else {
                b.append("[...]");
            }
            
            if (it.hasNext()) {
                b.append(", ");
            }
        }
        b.append("]");
        return b.toString();
    }
    
    
    
    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T t : c) {
            this.add(t);
        }
        return true;
    }

    
    
    @Override
    public void clear() {
        this.start = 0;
        this.size = 0;
    }

    
    
    @Override
    public boolean contains(Object o) {
        for (Object obj : this.buffer) {
            if (obj != null && obj.equals(o)) {
                return true;
            }
        }
        return false;
    }

    
    
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!this.contains(obj)) {
                return false;
            }
        }
        return true;
    }

    
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int modc = modCount;
            int r = start;
            int steps = 0;
            final int maxSteps = size;
            
            
            
            @Override
            public boolean hasNext() {
                return this.steps < this.maxSteps;
            }

            
            
            @Override
            public T next() {
                if (this.modc != modCount) {
                    throw new ConcurrentModificationException();
                } else if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final int ri = this.r;
                this.r = (this.r + 1) % cap;
                ++this.steps;
                return buffer[ri];
            }

            
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }
    
    

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    
    
    @Override
    public int size() {
        return this.size;
    }

    
    
    @Override
    public Object[] toArray() {
        final Object[] result = new Object[this.size];
        int i = 0;
        for (Object o : this) {
            result[i++] = o;
        }
        return result;
    }
    
    

    @SuppressWarnings("unchecked")
    @Override
    public <E> E[] toArray(E[] a) {
        if (a.length < this.size) {
            a = (E[])java.lang.reflect.Array.newInstance(
                a.getClass().getComponentType(), this.size);
        }
        int i = 0;
        for (T o : this) {
            a[i++] = (E) o;
        }
        return a;
    }

    
    
    @Override
    public boolean add(T e) {
        ++this.modCount;
        final int next = (this.start + this.size) % this.cap;
        if (this.size != this.cap) {
            this.size++;
        } else {
            this.start = (next + 1) % this.cap;
        }
        this.buffer[next] = e;
        return true;
    }
    
    

    @Override
    public T element() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.buffer[this.start];
    }

    
    
    @Override
    public boolean offer(T e) {
        return this.add(e);
    }

    
    
    @Override
    public T peek() {
        if (this.isEmpty()) {
            return null;
        }
        return this.buffer[this.start];
    }

    
    
    @Override
    public T poll() {
        if (this.isEmpty()) {
            return null;
        }
        ++this.modCount;
        final int r = this.start;
        this.start = (this.start + 1) % this.cap;
        --this.size;
        return this.buffer[r];
    }

    
    
    @Override
    public T remove() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.poll();
    }
}
