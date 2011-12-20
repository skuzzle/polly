package polly.core.formatting;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


import de.skuzzle.polly.sdk.FormatManager;

/* Note: This class is subject to ISSUE: 0000001 */

/**
 * @author Simon
 */
public class FormatManagerImpl implements FormatManager {

    private DateFormat defaultDateFormat;
    private NumberFormat defaultNumberFormat;
    
    
    
    public FormatManagerImpl(String dateFormatString, String numberFormatString) {
        this.defaultDateFormat = new SimpleDateFormat(dateFormatString);
        /*
         * ISSUE: 0000019
         */
        this.defaultDateFormat.setTimeZone(TimeZone.getTimeZone("ECT"));
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
        nf.applyPattern(numberFormatString);
        this.defaultNumberFormat = nf;
    }
    
    
    
    @Override
    public synchronized String formatNumber(double number) {
        return this.defaultNumberFormat.format(number);
    }

    
    
    @Override
    public synchronized String formatDate(Date date) {
        return this.defaultDateFormat.format(date);
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
        /*StringBuffer sb = new StringBuffer();
        long diffInSeconds = Math.abs(span);
        
        long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
        long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
        long years = (diffInSeconds = (diffInSeconds / 12));

        if (years > 0) {
            if (years == 1) {
                sb.append("ein Jahr");
            } else {
                sb.append(years + " Jahre");
            }
            if (years <= 6 && months > 0) {
                if (months == 1) {
                    sb.append(" und ein Monat");
                } else {
                    sb.append(" und " + months + " Monate");
                }
            }
        } else if (months > 0) {
            if (months == 1) {
                sb.append("ein Monat");
            } else {
                sb.append(months + " Monate");
            }
            if (months <= 6 && days > 0) {
                if (days == 1) {
                    sb.append(" und ein Tag");
                } else {
                    sb.append(" und " + days + " Tage");
                }
            }
        } else if (days > 0) {
            if (days == 1) {
                sb.append("ein Tag");
            } else {
                sb.append(days + " Tage");
            }
            if (days <= 3 && hrs > 0) {
                if (hrs == 1) {
                    sb.append(" und eine Stunde");
                } else {
                    sb.append(" und " + hrs + " Stunden");
                }
            }
        } else if (hrs > 0) {
            if (hrs == 1) {
                sb.append("eine Stunde");
            } else {
                sb.append(hrs + " Stunden");
            }
            if (min > 1) {
                sb.append(" und " + min + " Minuten");
            }
        } else if (min > 0) {
            if (min == 1) {
                sb.append("eine Minute");
            } else {
                sb.append(min + " Minuten");
            }
            if (sec > 1) {
                sb.append(" und " + sec + " Sekunden");
            }
        } else {
            if (sec <= 1) {
                sb.append("eine Sekunde");
            } else {
                sb.append(sec + " Sekunden");
            }
        }

        return sb.toString();*/
    }
}