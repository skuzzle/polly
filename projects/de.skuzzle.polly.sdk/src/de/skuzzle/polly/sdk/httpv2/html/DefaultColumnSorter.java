package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Comparator;

public class DefaultColumnSorter<T> implements HTMLColumnSorter<T> {



    @Override
    public Comparator<? super T> getComparator(final int column, 
            final HTMLTableModel<T> model) {
        
        return new Comparator<T>() {
            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public int compare(T o1, T o2) {
                Object val1 = model.getCellValue(column, o1);
                Object val2 = model.getCellValue(column, o2);
                if (val1 instanceof Comparable && val2 instanceof Comparable) {
                    Comparable c1 = (Comparable) val1;
                    Comparable c2 = (Comparable) val2;
                    return c1.compareTo(c2);
                } else {
                    if (val1 == null) {
                        val1 = "";
                    }
                    if (val2 == null) {
                        val2 = "";
                    }
                    return val1.toString().compareToIgnoreCase(val2.toString());
                }
            }
        };
    }
}
