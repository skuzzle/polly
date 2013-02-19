package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.lang.Function;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.parsing.util.TimeSpanFormat;
import de.skuzzle.polly.tools.streams.StringBuilderWriter;
import de.skuzzle.polly.tools.strings.IteratorPrinter;
import de.skuzzle.polly.tools.strings.IteratorPrinter.StringProvider;


public interface LiteralFormatter {
    
    /**
     * This formatter formats all literals so that the result will be parseable again.
     */
    public LiteralFormatter DEFAULT = new LiteralFormatter() {
        
        private final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.######");
        private final TimeSpanFormat TIMESPAN_FORMAT = new TimeSpanFormat(false);
        private final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "HH:mm@dd.MM.yyyy");
        
        
        
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



        @Override
        public String formatList(ListLiteral listLiteral) {
            StringBuilder b = new StringBuilder();
            b.append("{");
            IteratorPrinter.print(
                listLiteral.getContent(), ", ", 
                new StringProvider<Expression>() {
                    @Override
                    public String toString(Expression o) {
                        return Unparser.toString(o);
                    }
            }, new PrintWriter(new StringBuilderWriter(b)));
            b.append("}");
            return b.toString();
        }



        @Override
        public String formatFunction(FunctionLiteral functionLiteral) {
            final StringBuilder b = new StringBuilder();
            b.append("\\(");
            final Iterator<Declaration> it = functionLiteral.getFormal().iterator();
            while (it.hasNext()) {
                final Declaration formal = it.next();
                b.append(formal.getType().getName());
                b.append(" ");
                b.append(formal.getName());
                if (it.hasNext()) {
                    b.append(",");
                }
            }
            if (!(functionLiteral.getBody() instanceof Function)) {
                b.append(":");
                b.append(Unparser.toString(functionLiteral.getBody(), this));
            }
            b.append(")");
            return b.toString();
        }



        @Override
        public String formatHelp(HelpLiteral helpLiteral) {
            return "?";
        }
    };
    
    
    
    public String formatNumberLiteral(NumberLiteral number);
    
    public String formatDate(DateLiteral date);
    
    public String formatTimespan(TimespanLiteral timespan);

    public String formatChannel(ChannelLiteral channel);
    
    public String formatString(StringLiteral string);

    public String formatUser(UserLiteral user);

    public String formatList(ListLiteral listLiteral);

    public String formatFunction(FunctionLiteral functionLiteral);

    public String formatHelp(HelpLiteral helpLiteral);
}