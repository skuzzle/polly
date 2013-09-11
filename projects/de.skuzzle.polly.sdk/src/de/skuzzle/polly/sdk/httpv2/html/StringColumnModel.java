package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Comparator;

public class StringColumnModel<T> implements HTMLColumnModel<T> {

    
    public final Acceptor TO_STRING_ACCEPTOR = new Acceptor() {

        @Override
        public boolean accept(String filter, Object cellValue) {
            return cellValue.toString().toLowerCase().matches(
                ".*" + filter.toLowerCase() + ".*");
        }
    }; 


    
    private final HTMLTableModel<T> model;
    
    
    
    public StringColumnModel(HTMLTableModel<T> model) {
        this.model = model;
    }
    
    
    
    @Override
    public HTMLTableModel<T> getModel() {
        return this.model;
    }



    @Override
    public Comparator<? super T> getComparator(final int column) {
        return new Comparator<T>() {
            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public int compare(T o1, T o2) {
                Object val1 = getModel().getCellValue(column, o1);
                Object val2 = getModel().getCellValue(column, o2);
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



    @Override
    public Acceptor getAcceptor(int column) {
        return TO_STRING_ACCEPTOR;
    }
}
