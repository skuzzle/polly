package de.skuzzle.polly.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Properties class which stores its values sorted.
 * 
 * @author Simon
 * @since 0.6.1
 */
public class SortedProperties extends Properties {

    private static final long serialVersionUID = 1L;
    
    private final static Comparator<Object> UNSAFE_COMPARER = new Comparator<Object>() {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public int compare(Object o1, Object o2) {
            return ((Comparable) o1).compareTo((Comparable) o2);
        }
    };


    
    public SortedProperties() {
        super();
    }


    public SortedProperties(Properties defaults) {
        super(defaults);
    }



    @Override
    public synchronized Enumeration<Object> keys() {
        Collections.sort(Collections.list(super.keys()), UNSAFE_COMPARER);
        return super.keys();
    }
    
    
}
