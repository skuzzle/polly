package de.skuzzle.polly.config;

import java.util.Iterator;
import java.util.List;


class TypeFormatter {

    public String format(Object obj) {
        return this.format(obj, new StringBuilder());
    }
    
    
    
    public String format(Object obj, StringBuilder b) {
        if (obj instanceof List<?>) {
            Iterator<?> it = ((List<?>) obj).iterator();
            while (it.hasNext()) {
                b.append(this.format(it.next()));
                if (it.hasNext()) {
                    b.append(", ");
                }
            }
        } else {
            b.append(obj.toString());
        }
        return b.toString();
    }
    
}
