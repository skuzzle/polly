package de.skuzzle.polly.core.internal.formatting;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



import de.skuzzle.polly.core.parser.util.TimeSpanFormat;
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
        this.defaultDateFormat.setTimeZone(TimeZone.getTimeZone("ECT")); //$NON-NLS-1$
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
            return "Unknown"; //$NON-NLS-1$
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
        return new TimeSpanFormat(true).format(seconds);
    }
    
    
    
    @Override
    public String formatTimeSpanMs(long ms) {
        return this.formatTimeSpan(ms / 1000);
    }
    
    
    
    @Override
    public String formatBytes(long bytes) {
        if (bytes > MEGA_BYTE) {
            long mb = bytes / MEGA_BYTE;
            return mb + "MB " + this.formatBytes(bytes % MEGA_BYTE); //$NON-NLS-1$
        } else if (bytes > KILO_BYTE) {
            long kb = bytes / KILO_BYTE;
            return kb + "KB " + this.formatBytes(bytes % KILO_BYTE); //$NON-NLS-1$
        }
        return bytes + "B"; //$NON-NLS-1$
    }
}