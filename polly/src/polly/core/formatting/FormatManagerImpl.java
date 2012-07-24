package polly.core.formatting;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.FormatManager;

/* Note: This class is subject to ISSUE: 0000001 */

/**
 * @author Simon
 */
public class FormatManagerImpl implements FormatManager {

    private DateFormat defaultDateFormat;
    private NumberFormat defaultNumberFormat;
    
    
    
    public FormatManagerImpl(Configuration cfg) {
        this.defaultDateFormat = new SimpleDateFormat(
            cfg.readString(Configuration.DATE_FORMAT));
        /*
         * ISSUE: 0000019
         */
        this.defaultDateFormat.setTimeZone(TimeZone.getTimeZone("ECT"));
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
        nf.applyPattern(cfg.readString(Configuration.NUMBER_FORMAT));
        this.defaultNumberFormat = nf;
    }
    
    
    
    @Override
    public synchronized String formatNumber(double number) {
        return this.defaultNumberFormat.format(number);
    }

    
    
    @Override
    public synchronized String formatDate(Date date) {
        if (date == null) {
            return "Unknown";
        }
        return this.defaultDateFormat.format(date);
    }
    
    
    
    @Override
    public String formatDate(long timestamp) {
        return this.formatDate(new Date(timestamp));
    }
    
    
    
    public synchronized DateFormat getDefaultDateFormat() {
        return (DateFormat) this.defaultDateFormat.clone();
    }
    
    
    
    public synchronized NumberFormat getDefaultNumberFormat() {
        return (NumberFormat) this.defaultNumberFormat.clone();
    }



    @Override
    public String formatTimeSpan(long span) {
        return new TimeSpanFormat().format(span);
    }
}