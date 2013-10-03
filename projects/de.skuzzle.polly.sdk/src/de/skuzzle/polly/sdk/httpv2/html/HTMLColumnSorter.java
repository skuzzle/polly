package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Comparator;


public interface HTMLColumnSorter<T> {
    
    public Comparator<? super T> getComparator(int column, HTMLTableModel<T> model);
}
