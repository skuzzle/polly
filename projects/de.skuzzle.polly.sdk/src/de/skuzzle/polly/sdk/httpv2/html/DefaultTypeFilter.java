package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Date;

import de.skuzzle.polly.sdk.MyPolly;


public class DefaultTypeFilter extends TypeColumnFilter {

    public DefaultTypeFilter(HTMLTableModel<?> model, MyPolly myPolly) {
        super(model);
        this.addAcceptor(Date.class, new AllDayAcceptor(myPolly));
        this.addAcceptor(Object.class, DefaultColumnFilter.REGEX_ACCEPTOR);
        this.addAcceptor(String.class, DefaultColumnFilter.REGEX_ACCEPTOR);
        this.addAcceptor(Boolean.class, DefaultColumnFilter.REGEX_ACCEPTOR);
        this.addAcceptor(Double.class, DefaultColumnFilter.REGEX_ACCEPTOR);
    }

}
