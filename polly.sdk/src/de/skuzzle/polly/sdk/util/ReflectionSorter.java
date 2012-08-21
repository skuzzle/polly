package de.skuzzle.polly.sdk.util;

import java.util.Collections;
import java.util.List;


/**
 * This class provides static helper methods to sort lists of any type for any
 * sort key using a {@link ReflectionComparator}.
 * 
 * @author Simon
 * @since 0.9.1
 */
public final class ReflectionSorter {

    
    /**
     * <p>Sorts the given list for the given sort key. As this method uses reflection it 
     * is pretty slow and should only be used in exceptional cases.</p>
     * 
     * <p>Note that the {@code sortKey} is interpreted as a getter Method of the type 
     * that will be sorted. The return type of this getter must implement the 
     * {@link Comparable} interface.</p>
     * 
     * <p>This method by default sorts ascending. Use 
     * {@link #sort(List, String, boolean)} to specify a sort direction.</p>
     * 
     * @param list The elements to sort. The list will be modified so that its sorted 
     *          after execution of this method.
     * @param sortKey The sort key to sort the list for.
     */
    public static <T> void sort(List<T> list, String sortKey) {
        sort(list, sortKey, false);
    }
    
    
    
    /**
     * <p>Sorts the given list for the given sort key and sort direction. As this method 
     * uses reflection it is pretty slow and should only be used in exceptional cases.</p>
     * 
     * <p>Note that the {@code sortKey} is interpreted as a getter Method of the type 
     * that will be sorted. The return type of this getter must implement the 
     * {@link Comparable} interface.</p>
     * 
     * @param list The elements to sort. The list will be modified so that its sorted 
     *          after execution of this method.
     * @param sortKey The sort key to sort the list for.
     * @param desc If <code>true</code>, the list will be sorted descending, otherwise it
     *          will be sorted ascending.
     */
    public static <T> void sort(List<T> list, String sortKey, boolean desc) {
        ReflectionComparator rc = new ReflectionComparator(sortKey, desc);
        Collections.sort(list, rc);
    }
    
    
    
    private ReflectionSorter() {}
}