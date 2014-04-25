package de.skuzzle.polly.sdk.httpv2.html;

import de.skuzzle.polly.sdk.Types.NumberType;

import de.skuzzle.polly.sdk.Types;


public class NumericAcceptor implements Acceptor {
    
    private static class FilterObject {
        int order;
        double number;
        boolean noFilter;
        
        private FilterObject(double number, int order, boolean noFilter) {
            this.order = order;
            this.number = number;
            this.noFilter = noFilter;
        }
        
        
        
        public boolean test(double number) {
            if (this.noFilter) {
                return true;
            } else if (this.order < 0) {
                return number < this.number;
            } else if (this.order > 0) {
                return number > this.number;
            } else {
                return Double.compare(this.number, number) == 0;
            }
        }
    }
    

    
    @Override
    public Object parseFilter(String filter) {
        if (filter == null || filter.equals("")) { //$NON-NLS-1$
            return new FilterObject(0, 0, true);
        }
        int order = 0;
        String part = filter;
        if (filter.startsWith("<")) { //$NON-NLS-1$
            order = -1;
            part = filter.substring(1);
        } else if (filter.startsWith(">")) { //$NON-NLS-1$
            order = 1;
            part = filter.substring(1);
        }
        
        try {
            final double d = Double.parseDouble(part);
            return new FilterObject(d, order, false);
        } catch (NumberFormatException e) {
            // backup, better not happen
            return new FilterObject(0.0, 0, true);
        }
    }
    
    

    @Override
    public boolean accept(Object filter, Object cellValue) {
        if (cellValue == null || !(filter instanceof FilterObject)) {
            return false;
        }
        
        final double currentValue;
        if (cellValue instanceof Types.NumberType) {
            currentValue = ((NumberType) cellValue).getValue();
        } else if (cellValue instanceof Number) {
            currentValue = ((Number) cellValue).doubleValue();
        } else {
            return false;
        }
        
        final FilterObject fo = (FilterObject) filter;
        return fo.test(currentValue);
    }
}
