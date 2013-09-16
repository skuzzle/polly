package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Date;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.DateType;
import de.skuzzle.polly.sdk.time.DateUtils;

public class AllDayAcceptor implements Acceptor {

    private final MyPolly myPolly;



    public AllDayAcceptor(MyPolly myPolly) {
        this.myPolly = myPolly;
    }



    @Override
    public boolean accept(String filter, Object cellValue) {
        final Types d = this.myPolly.parse(filter);
        if (d instanceof DateType) {
            final DateType date = (DateType) d;
            final Date current = (Date) cellValue;
            return DateUtils.isSameDay(current, date.getValue());
        }
        return false;
    }
}
