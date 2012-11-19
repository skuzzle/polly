package de.skuzzle.polly.tools;

import java.util.Comparator;


public final class CompareUtils {
    

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
     * @param type The comparable type of the elements to compre.
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