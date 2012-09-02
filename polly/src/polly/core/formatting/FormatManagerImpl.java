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

    private final static long KILO_BYTE = 1L << 10L; 
    private final static long MEGA_BYTE = 1L << 20L;
    
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
    public String formatTimeSpan(long seconds) {
        return new TimeSpanFormat().format(seconds);
    }
    
    
    
    @Override
    public String formatTimeSpanMs(long ms) {
        return this.formatTimeSpan(ms / 1000);
    }
    
    
    
    @Override
    public String formatBytes(long bytes) {
        if (bytes > MEGA_BYTE) {
            bytes /= MEGA_BYTE;
            return bytes + " MB";
        } else if (bytes > KILO_BYTE) {
            bytes /= KILO_BYTE;
            return bytes + " KB";
        }
        return bytes + " B";
    }
}