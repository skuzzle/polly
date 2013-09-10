package de.skuzzle.polly.sdk.util;

import java.util.Comparator;


public class DirectedComparator<T> implements Comparator<T> {

    public enum SortOrder {
        UNDEFINED,
        ASCENDING,
        DESCENDING;
        
        public SortOrder reverse() {
            switch (this) {
            case ASCENDING: return DESCENDING;
            default:
            case DESCENDING: return ASCENDING;
            }
        }
    }
    
    private final SortOrder order;
    private final Comparator<T> wrapped;
    
    public DirectedComparator(SortOrder order, Comparator<T> wrapped) {
        this.order = order;
        this.wrapped = wrapped;
    }
    
    
    
    @Override
    public int compare(T o1, T o2) {
        switch (this.order) {
        default:
        case UNDEFINED: return 0;
        case ASCENDING: return this.wrapped.compare(o1, o2);
        case DESCENDING: return this.wrapped.compare(o2, o1);
        }
    }
}
