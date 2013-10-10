package de.skuzzle.polly.sdk.httpv2;

import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.util.ColumnFilter;


public abstract class StringColumnFilter<T> implements ColumnFilter<T> {

    
    @Override
    public boolean accept(String[] filterString, T element) {
        boolean result = true;
        for (int i = 0; i < filterString.length; ++i) {
            if (!filterString[i].equals("")) { //$NON-NLS-1$
                final Pattern p = Pattern.compile(".*" + filterString + ".*",  //$NON-NLS-1$ //$NON-NLS-2$
                    Pattern.CASE_INSENSITIVE);
                
                final Object value = this.getCellValue(i, element);
                result &= p.matcher(value.toString()).matches();
            }
        }
        return result;
    }
    
    
    protected abstract Object getCellValue(int column, T data);
}
