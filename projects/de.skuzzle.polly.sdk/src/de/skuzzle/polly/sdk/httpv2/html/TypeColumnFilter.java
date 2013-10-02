package de.skuzzle.polly.sdk.httpv2.html;

import java.util.HashMap;
import java.util.Map;

public class TypeColumnFilter implements HTMLColumnFilter {

    private final Map<Class<?>, Acceptor> acceptors;
    private final HTMLTableModel<?> model;



    public TypeColumnFilter(HTMLTableModel<?> model) {
        this.acceptors = new HashMap<Class<?>, Acceptor>();
        this.model = model;
    }



    public void addAcceptor(Class<?> cls, Acceptor acc) {
        this.acceptors.put(cls, acc);
    }



    @Override
    public Acceptor getAcceptor(int column) {
        final Class<?> colClass = this.model.getColumnClass(column);
        final Acceptor acc = this.acceptors.get(colClass);
        if (acc == null) {
            return DefaultColumnFilter.REGEX_ACCEPTOR;
        }
        return acc;
    }

}
