package commands;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import polly.core.Messages;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.time.DateUtils;



public class CalendarCommand extends Command {
    


    public CalendarCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "cal"); //$NON-NLS-1$
        this.createSignature(Messages.calendarSig0Desc, 
            new Parameter(Messages.calendarSig0Date, Types.DATE));
        this.setHelpText(Messages.calendarHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            Date d = signature.getDateValue(0);
            String[] lines = calendarString(d).split("\n"); //$NON-NLS-1$
            for (String line : lines) {
                this.reply(channel, line);
            }
        }
        return false;
    }

    
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        
        c.set(Calendar.MONTH, 4);
        System.out.println(calendarString(c.getTime()));
    }
    
    
    private static String calendarString(Date d) {
        StringBuilder b = new StringBuilder();
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(d);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);        
        System.out.println(firstDay.getTime());
        Calendar lastMonth = Calendar.getInstance();
        lastMonth.setTime(d);
        lastMonth.add(Calendar.MONTH, -1);
        
        // days last month
        int ld = lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        lastMonth.set(Calendar.DAY_OF_MONTH, ld);

        /*
         * no one will ever get what happens here
         */
        lastMonth.add(Calendar.DAY_OF_MONTH, -diff(firstDay.get(Calendar.DAY_OF_WEEK)));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy"); //$NON-NLS-1$
        b.append(Messages.bind(Messages.calendarFor, sdf.format(d)));
        b.append("\n"); //$NON-NLS-1$
        b.append("\u0002\u001fKW | Mo Di Mi Do Fr Sa So\n"); //$NON-NLS-1$
        
        System.out.println(lastMonth.getTime());
        for (int j = 0; j < 6; ++j) {
            b.append(pad(lastMonth.get(Calendar.WEEK_OF_YEAR)));
            b.append(" | "); //$NON-NLS-1$
            for (int i = 0; i < 7; ++i) {
                String prefix = ""; //$NON-NLS-1$
                String postfix = ""; //$NON-NLS-1$
                if (lastMonth.before(firstDay) || 
                        lastMonth.get(Calendar.MONTH) > firstDay.get(Calendar.MONTH)) {
                    prefix = "\u000314"; //$NON-NLS-1$
                    postfix = "\u000f"; //$NON-NLS-1$
                } else if (DateUtils.isToday(lastMonth.getTime())) {
                    prefix = "\u0002\u000304"; //$NON-NLS-1$
                    postfix = "\u000f"; //$NON-NLS-1$
                } else if (DateUtils.isSameDay(lastMonth.getTime(), d)) {
                    prefix = "\u0002"; //$NON-NLS-1$
                    postfix = "\u000f"; //$NON-NLS-1$
                }
                b.append(prefix);
                b.append(pad(lastMonth.get(Calendar.DAY_OF_MONTH)));
                b.append(postfix);
                b.append(" "); //$NON-NLS-1$
                lastMonth.add(Calendar.DAY_OF_MONTH, 1);
            }
            b.append("\n"); //$NON-NLS-1$
        }
        
        return b.toString();
    }
    
    
    private static int diff(int day) {
        switch (day) {
        case Calendar.MONDAY: return -1;
        case Calendar.TUESDAY: return 0;
        case Calendar.WEDNESDAY: return 1;
        case Calendar.THURSDAY: return 2;
        case Calendar.FRIDAY: return 3;
        case Calendar.SATURDAY: return 4;
        case Calendar.SUNDAY: return 5;
        default: return 0;
        }
    }
    
    
    private static String pad(int num) {
        if (num < 10) {
            return "0" + Integer.toString(num); //$NON-NLS-1$
        }
        return Integer.toString(num);
    }
}
