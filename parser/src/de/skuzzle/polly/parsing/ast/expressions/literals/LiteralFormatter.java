package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import de.skuzzle.polly.parsing.util.TimeSpanFormat;


public interface LiteralFormatter {
    
    /**
     * This formatter formats all literals so that the result will be parseable again.
     */
    public LiteralFormatter DEFAULT = new LiteralFormatter() {
        
        private final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.######");
        private final TimeSpanFormat TIMESPAN_FORMAT = new TimeSpanFormat(false);
        private final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "hh:mm@dd.MM.yyyy");
        
        @Override
        public String formatNumberLiteral(NumberLiteral number) {
            final double val = number.getValue();
            if (Math.round(val) == val) {
                int intVal = (int) val;
                return Integer.toString(intVal, number.getRadix());
            }
            return NUMBER_FORMAT.format(val);
        }
        
        

        @Override
        public String formatDate(DateLiteral date) {
            return DATE_FORMAT.format(date.getValue());
        }

        
        
        @Override
        public String formatTimespan(TimespanLiteral timespan) {
            return TIMESPAN_FORMAT.format(timespan.getSeconds());
        }



        @Override
        public String formatChannel(ChannelLiteral channelLiteral) {
            return "#" + channelLiteral.getValue();
        }



        @Override
        public String formatString(StringLiteral string) {
            return "\"" + string.getValue() + "\"";
        }



        @Override
        public String formatUser(UserLiteral user) {
            return "@" + user.getValue();
        }
    };
    
    
    
    public String formatNumberLiteral(NumberLiteral number);
    
    public String formatDate(DateLiteral date);
    
    public String formatTimespan(TimespanLiteral timespan);

    public String formatChannel(ChannelLiteral channel);
    
    public String formatString(StringLiteral string);

    public String formatUser(UserLiteral user);
}