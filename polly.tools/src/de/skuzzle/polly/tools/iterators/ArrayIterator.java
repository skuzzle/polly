package de.skuzzle.polly.tools.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Iterator class to iterate over arrays. This {@link Iterator} implementation provides
 * additional convenience methods for replacing the current value and iterating backwards.
 * Using a for...each loop, this class will intentionally iterate a given array from
 * the beginning to the end.
 * 
 * @author Simon
 */
public class ArrayIterator<T> implements Iterable<T>, Iterator<T> {

    public static <T> ArrayIterator<T> get(T[] array) {
        return new ArrayIterator<T>(array, 0, array.length);
    }
    
    
    public static <T> ArrayIterator<T> forRange(T[] array, int start) {
        return new ArrayIterator<T>(array, start, array.length);
    }
    
    
    public static <T> ArrayIterator<T> forRange(T[] array, int start, int end) {
        return new ArrayIterator<T>(array, start, end);
    }

    
    private int i;
    private int start;
    private int end;
    private T[] array;
    
    
    
    /**
     * Creates a new ArrayIterator
     * @param array The array to iterate over
     * @param start The inclusive index to begin iteration at.
     * @param end The exclusive index to end iteration at.
     * @throws IllegalArgumentException If start and end indices are out of array range.
     */
    private ArrayIterator(T[] array, int start, int end) {
        if (end > array.length) {
            throw new IllegalArgumentException("end > length");
        } else if (start < 0) {
            throw new IllegalArgumentException("start < 0");
        }
        this.array = array;
        this.start = start;
        this.i = start;
        this.end = end;
    }
    
    
    
    /**
     * Moves iteration to the first element, so the next call to {@link #next()} returns
     * the first element.
     */
    public void first() {
        this.i = this.start;
    }
    
    
    
    /**
     * Moves iteration to the last element, so the next call to {@link #hasNext()} 
     * returns false.
     */
    public void last() {
        this.i = this.end;
    }
    
    
    
    /**
     * Returns the next element without moving the iteration pointer.
     * @return The element which will be returned by the next call to {@link #next()}.
     */
    public T peekNext() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("index " + this.i);
        }
        return this.array[this.i];
    }
    
    
    
    /**
     * Determines whether there is an element next to current iteration position.
     * @return <code>true</code> if iteration has not reached the end of the array.
     */
    @Override
    public boolean hasNext() {
        return this.i < this.end;
    }
    
    
    
    /**
     * Determines whether there is an element previous to current iteration position.
     * @return <code>true</code> if iteration is not at the beginning and the array
     *      contains at least one element.
     */
    public boolean hasPrevious() {
        return this.i > 0;
    }

    
    
    /**
     * Returns the next element in this iteration. If there are no more elements,
     * an exception is thrown.
     * @return The next array element.
     * @throws NoSuchElementException If there are no more elements.
     */
    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("index " + this.i);
        }
        return this.array[this.i++];
    }
    
    
    
    /**
     * Returns the previous element in this iteration, decreasing the current iteration
     * pointer by one.
     * @return The previous element.
     * @throws NoSuchElementException If there is no previous element.
     */
    public T previous() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException("index " + this.i);
        }
        return this.array[--i];
    }
    
    
    
    /**
     * Returns the current element of the iteration. That is the same as returned by the
     * preceded call to {@link #next()}.
     * 
     * @return The current element of iteration.
     */
    public T current() {
        int index = Math.max(this.start, this.i - 1);
        return this.array[index];
    }
    
    
    
    /**
     * Replaces the element at the current position with the given value.
     * @param other The new value to replace the current elements value with.
     */
    public void replace(T other) {
        int index = Math.max(this.start, i - 1);
        this.array[index] = other;
    }
    

    
    /**
     * This method is not supported, thus it will always throw an
     * {@link UnsupportedOperationException}.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
    
    
    
    /**
     * Moves current iteration to given index. The next call of {@link #next()} will 
     * return the value at position i.
     * 
     * @param i The new iteration position.
     * @throws IllegalArgumentException If the given number is not in the range specified
     *      in the constructor.
     */
    public void move(int i) {
        this.checkRange(i);
        this.i = i;
    }
    
    
    
    /**
     * Returns the index of the current iteration.
     * @return The iteration index.
     */
    public int getIndex() {
        return this.i;
    }
    
    
    
    /**
     * Swaps the element at the current position with the next element.
     * @throws NoSuchElementException If there is no next element.
     */
    public void swapNext() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("index " + (this.i + 1));
        }
        this.swap(this.i, this.i + 1);
    }
    
    
    
    /**
     * Swaps the element at the current position with the previous element.
     * @throws NoSuchElementException If there is no previous element.
     */
    public void swapPrevious() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException("index " + (this.i - 1));
        }
        this.swap(this.i, Math.max(this.i - 1, this.start));
    }
    
    
    
    private void swap(int from, int with) {
        this.checkRange(from);
        this.checkRange(with);
        T tmp = this.array[from];
        this.array[from] = this.array[with];
        this.array[with] = tmp;
    }
    
    
    
    private void checkRange(int i) {
        if (i < this.start || i >= this.end) {
            throw new IllegalArgumentException("Out of range. " + i);
        }
    }
    
    
    
    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
