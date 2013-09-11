package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Comparator;


public interface HTMLColumnModel<T> {
    
    public HTMLTableModel<T> getModel();
    
    public Comparator<? super T> getComparator(int column);
    
    public Acceptor getAcceptor(int column);
}
