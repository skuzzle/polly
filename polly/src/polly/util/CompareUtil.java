package polly.util;

import java.util.Comparator;


public class CompareUtil {
    
    public static <T extends Comparable<T>> Comparator<T> getComparator(final T c) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
    }
    
}
