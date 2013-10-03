package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Date;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types.DateType;
import de.skuzzle.polly.sdk.time.DateUtils;

public class AllDayAcceptor implements Acceptor {

    private final MyPolly myPolly;



    public AllDayAcceptor(MyPolly myPolly) {
        this.myPolly = myPolly;
    }

    
    
    @Override
    public Object parseFilter(String filter) {
        return this.myPolly.parse(filter);
    }

    

    @Override
    public boolean accept(Object filter, Object cellValue) {
        if (filter instanceof DateType) {
            final DateType date = (DateType) filter;
            final Date current = (Date) cellValue;
            return DateUtils.isSameDay(current, date.getValue());
        }
        return false;
    }
}
