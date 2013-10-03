package http;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.html.Acceptor;
import de.skuzzle.polly.sdk.httpv2.html.AllDayAcceptor;
import de.skuzzle.polly.sdk.httpv2.html.DefaultColumnFilter;

public class RemindTableFilter extends DefaultColumnFilter {

    private final Acceptor allDayAcceptor;



    public RemindTableFilter(MyPolly myPolly) {
        this.allDayAcceptor = new AllDayAcceptor(myPolly);
    }



    @Override
    public Acceptor getAcceptor(int column) {
        if (column == 2 || column == 3) {
            return this.allDayAcceptor;
        }
        return super.getAcceptor(column);
    }
}
