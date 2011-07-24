package polly.util;

public class TimeSpanFormat {
    
    
    public final static long SECONDS = 1L;
    public final static long MINUTES = 60L;
    public final static long HOURS = 60L;
    public final static long DAYS = 24L;
    public final static long WEEKS = 7L;
    public final static long YEARS = 365L;
    
    /*
     * y = years (if > 0)
     * d = days (if > 0)
     * h = hours (if > 0)
     * m = minutes (if > 0)
     * s = seconds (if > 0)
     * 
     * yn
     */
    private String pattern = "%[y]%[yn]%[d]%[dn]%h:%m:%s";
    
    public String format(long seconds) {
        String copy = new String(this.pattern);
        long years = this.yearPart(seconds);
        long days = this.dayPart(seconds);
        long hours = this.hourPart(seconds);
        long minutes = this.minutePart(seconds);
        long s = this.secondPart(seconds);
        String s1 = "";
        String s2 = "";


        s1 = days > 0 ? this.dayString(days) : "";
        s2 = days > 0 ? Long.toString(days) : "";
        copy = copy.replaceAll("%\\[dn\\]", s1);
        copy = copy.replaceAll("%\\[d\\]", s2);
        copy = copy.replaceAll("%dn", this.dayString(years));
        copy = copy.replaceAll("%d", Long.toString(days));
        
        s1 = hours > 0 ? this.hourString(hours) : "";
        s2 = hours > 0 ? Long.toString(hours) : "";
        copy = copy.replaceAll("%\\[hn\\]", s1);
        copy = copy.replaceAll("%\\[h\\]", s2);
        copy = copy.replaceAll("%hn", this.hourString(hours));
        copy = copy.replaceAll("%h", Long.toString(hours));
        
        s1 = minutes > 0 ? this.minuteString(minutes) : "";
        s2 = minutes > 0 ? Long.toString(minutes) : "";
        copy = copy.replaceAll("%\\[mn\\]", s1);
        copy = copy.replaceAll("%\\[m\\]", s2);
        copy = copy.replaceAll("%mn", this.minuteString(minutes));
        copy = copy.replaceAll("%m", Long.toString(minutes));
        
        s1 = s > 0 ? this.secondsString(s) : "";
        s2 = s > 0 ? Long.toString(s) : "";
        copy = copy.replaceAll("%\\[sn\\]", s1);
        copy = copy.replaceAll("%\\[s\\]", s2);
        copy = copy.replaceAll("%sn", this.secondsString(s));
        copy = copy.replaceAll("%s", Long.toString(s));
        
        s1 = years > 0 ? this.yearString(years) : "";
        s2 = years > 0 ? Long.toString(years) : "";
        copy = copy.replaceAll("%\\[yn\\]", s1);
        copy = copy.replaceAll("%\\[y\\]", s2);
        copy = copy.replaceAll("%yn", this.yearString(years));
        copy = copy.replaceAll("%y", Long.toString(years));
        
        return copy;
    }
    
    
    protected String secondsString(long seconds) {
        if (seconds == 1) {
            return "Sekunde";
        }
        return "Sekunden";
    }
    
    
    
    protected String minuteString(long minutes) {
        if (minutes == 1) {
            return "Minuten";
        }
        return "Minuten";
    }
    
    
    
    protected String hourString(long hours) {
        if (hours == 1) {
            return "Stunde";
        }
        return "Stunden";
    }
    
    
    
    protected String dayString(long days) {
        if (days == 1) {
            return "Tag";
        }
        return "Tage";
    }
    
    
    
    protected String yearString(long years) {
        if (years == 1) {
            return "Jahr";
        }
        return "Jahre";
    }
    
    
    
    protected long secondPart(long seconds) {
        return seconds >= MINUTES ? seconds % MINUTES : seconds;
    }
    
    
    protected long minutePart(long seconds) {
        long minutes = seconds / MINUTES;
        return minutes >= HOURS ? minutes % HOURS : minutes;
    }
    
    
    protected long hourPart(long seconds) {
        long hours = seconds / HOURS / MINUTES;
        return hours >= DAYS ? hours % DAYS : hours;
    }
    
    
    protected long dayPart(long seconds) {
        long days = seconds / DAYS / HOURS / MINUTES;
        return days >= YEARS ? days % YEARS : days;
    }
    
    
    protected long yearPart(long seconds) {
        long years = seconds / YEARS / DAYS / HOURS / MINUTES;
        return years;
    }
}