package core;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;



/**
 * Provides useful static methods for working with dates. Its based on the
 * {@link GregorianCalendar} class of the java framework.
 * 
 * @author Simon
 */
public class DateUtils {
    
    
    public static Calendar calendarForDate(Date d, TimeZone zone) {
        Calendar cal = new GregorianCalendar(zone);
        cal.setTime(d);
        return cal;
    }
    
    
    
    public static Calendar calendarForDate(Date d) {
        return DateUtils.calendarForDate(d, TimeZone.getDefault());
    }
    
    
    
    public static Date dateFor(int day, int month, int year) {
        Calendar then = new GregorianCalendar(year, month, day);
        return then.getTime();
    }
    
    
    
    public static int getYear(Date d) {
        Calendar then = DateUtils.calendarForDate(d);
        return then.get(Calendar.YEAR);
    }
    
    
    
    public static int getYear() {
        return DateUtils.getYear(new Date());
    }
    
    
    
    public static int getMonth(Date d) {
        Calendar then = DateUtils.calendarForDate(d);
        return then.get(Calendar.MONTH);
    }
    
    
    
    public static int getMonth() {
        return DateUtils.getMonth(new Date());
    }
    
    
    
    public static int getDay(Date d) {
        Calendar then = DateUtils.calendarForDate(d);
        return then.get(Calendar.DAY_OF_MONTH);
    }
    
    
    
    public static int getDay() {
        return DateUtils.getDay(new Date());
    }
    
    
    
    public static Date timeFor(int hours, int minutes, int seconds) {
        Calendar then = new GregorianCalendar();
        then.set(Calendar.HOUR_OF_DAY, hours);
        then.set(Calendar.MINUTE, minutes);
        then.set(Calendar.SECOND, seconds);
        return then.getTime();
    }
    
    
    
    public static boolean isSameDay(Date d1, Date d2) {
        boolean day = getDay(d1) == getDay(d2);
        boolean month = getMonth(d1) == getMonth(d2);
        boolean year = getYear(d1) == getYear(d2);
        
        return day && month && year;
    }
    
    

    public static boolean isToday(Date d) {
        return isSameDay(new Date(), d);
    }
}
