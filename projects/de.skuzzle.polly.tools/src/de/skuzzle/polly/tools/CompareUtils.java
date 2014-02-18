package de.skuzzle.polly.tools;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class provides static utilities for working with the {@link Comparator} and
 * {@link Comparable} interfaces.
 *  
 * @author Simon Taddiken
 */
public final class CompareUtils {
    
    /**
     * Comparator implementation that uses several internal comparators which are 
     * executed as long as the next comparator returns 0.
     * If all comparators return 0, 0 will be returned. Otherwise, the first result != 0
     * will be returned.
     * 
     * @author Simon Taddiken
     * @param <T> Type to compare.
     */
    private final static class MultiComparator<T> implements Comparator<T> {

        private final List<Comparator<T>> comps;
        
        /**
         * Creates a new MultiComparator which uses the given list of comparators.
         * 
         * @param comps The comparators to execute in the order they occur in the list.
         */
        public MultiComparator(List<Comparator<T>> comps) {
            this.comps = comps;
        }
        
        
        
        @Override
        public int compare(T o1, T o2) {
            for (final Comparator<T> c : this.comps) {
                int r = c.compare(o1, o2);
                if (r != 0) {
                    return r;
                }
            }
            return 0;
        }
    }
    
    
    
    /**
     * Creates a comparator that internally uses the list of given comparators to
     * compare to objects. The returned comparator will return 0 if, and only if all
     * comparators in the given list return 0. Otherwise, it will return the first
     * result other than zero that any of the comparators in the list returned. 
     * Comparators are called in order they occur in the list.
     * 
     * @param comps The list of comparators to use.
     * @return A comparator instance.
     */
    public final static <T> Comparator<T> multiComparator(List<Comparator<T>> comps) {
        return new MultiComparator<T>(comps);
    }
    

    
    /**
     * Creates a comparator that internally uses the array of given comparators to
     * compare to objects. The returned comparator will return 0 if, and only if all
     * comparators in the given array return 0. Otherwise, it will return the first
     * result other than zero that any of the comparators in the array returned. 
     * Comparators are called in order they occur in the array.
     * 
     * @param comps The list of comparators to use.
     * @return A comparator instance.
     */
    @SafeVarargs
    public final static <T> Comparator<T> multiComparator(Comparator<T>... comps) {
        return new MultiComparator<T>(Arrays.asList(comps));
    }
    
    

    /**
     * Creates a reversed comparator to the given one. That means the resulting 
     * comparator sorts elements in the opposite direction.
     *  
     * @param c The source comparator.
     * @return A new comparator with reversed compare direction.
     */
    public final static <T extends Comparator<T>> Comparator<T> reverse(final T c) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return c.compare(o2, o1);
            }
        };
    }
    
    
    
    /**
     * Creates a {@link Comparator} from a {@link Comparable} object. 
     * 
     * @param type The comparable type of the elements to compare.
     * @return A new comparator that uses the {@link Comparable#compareTo(Object)} method
     *          to compare two elements.
     */
    public final static <T extends Comparable<T>> Comparator<T> fromComparable(
            final Class<T> type) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
    }
    
    
    
    /**
     * Compares any object by the result of their toString methods.
     */
    public final static Comparator<Object> TO_STRING_COMPARATOR = 
        new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        };
        
        
        
    
    private CompareUtils() {}
}