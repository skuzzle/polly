package de.skuzzle.polly.sdk.httpv2.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class HTMLInput {

    private final Map<String, String> attributes;
    private String content;
    
    
    public HTMLInput() {
        this.attributes = new HashMap<>();
    }
    
    
    
    public HTMLInput attr(String name, String val) {
        this.attributes.put(name, val);
        return this;
    }
    
    
    
    public HTMLInput attr(String name) {
        return this.attr(name, null);
    }
    
    
    
    public HTMLInput content(String content) {
        this.content = content;
        return this;
    }
    
    
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("<input ");
        for (final Entry<String, String> e : this.attributes.entrySet()) {
            b.append (e.getKey());
            b.append(" ");
            if (e.getValue() != null) {
                b.append("=\"");
                b.append(e.getValue());
                b.append("\" ");
            }
        }
        if (this.content != null) {
            b.append(">");
            b.append(this.content);
            b.append("</input>");
        } else {
            b.append("/>");
        }
        return b.toString();
    }
}
