package http;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.html.Acceptor;
import de.skuzzle.polly.sdk.httpv2.html.AllDayAcceptor;
import de.skuzzle.polly.sdk.httpv2.html.DefaultColumnFilter;

public class AllDayFilter extends DefaultColumnFilter {

    private final Acceptor acceptor;



    public AllDayFilter(MyPolly myPolly) {
        this.acceptor = new AllDayAcceptor(myPolly);
    }



    @Override
    public Acceptor getAcceptor(int column) {
        if (column == 0) {
            return this.acceptor;
        }
        return super.getAcceptor(column);
    }
}
